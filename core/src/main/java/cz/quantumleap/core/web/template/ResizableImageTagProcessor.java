package cz.quantumleap.core.web.template;

import com.google.common.collect.ImmutableSet;
import cz.quantumleap.core.filestorage.FileStorageManager;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class ResizableImageTagProcessor extends AbstractAttributeTagProcessor {

    private enum ResizeStrategy {
        LIMIT, CROP
    }

    public static final Logger log = LoggerFactory.getLogger(ResizableImageTagProcessor.class);

    private static final String ATTR_NAME = "resize";
    private static final int PRECEDENCE = 10000;

    private static final Set<String> SUPPORTED_FORMATS = ImmutableSet.of("JPG", "JPEG", "PNG", "GIF", "BMP", "WBMP");
    private static final int MAX_IMAGE_SIZE = 3000;

    private final FileStorageManager fileStorageManager;

    ResizableImageTagProcessor(String dialectPrefix, FileStorageManager fileStorageManager) {
        super(TemplateMode.HTML, dialectPrefix, "img", false, ATTR_NAME, true, PRECEDENCE, true);
        this.fileStorageManager = fileStorageManager;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        ResizeStrategy resizeStrategy = StringUtils.isBlank(attributeValue) ? ResizeStrategy.LIMIT : ResizeStrategy.valueOf(attributeValue.toUpperCase());
        String url = tag.getAttributeValue("src");
        Integer width = tag.hasAttribute("width") ? Integer.parseInt(tag.getAttributeValue("width")) : null;
        Integer height = tag.hasAttribute("height") ? Integer.parseInt(tag.getAttributeValue("height")) : null;

        if (StringUtils.isBlank(url) || !SUPPORTED_FORMATS.contains(FilenameUtils.getExtension(url).toUpperCase())) {
            return;
        }

        Validate.isTrue(resizeStrategy == ResizeStrategy.LIMIT || resizeStrategy == ResizeStrategy.CROP, "Unknown resize strategy " + resizeStrategy + "! It must be \"limit\" or \"crop\".");
        switch (resizeStrategy) {
            case LIMIT:
                Validate.isTrue(width != null || height != null, "At least one of the width or height must be specified!");
                break;
            case CROP:
                Validate.notNull(width, "Width attribute must be set!");
                Validate.notNull(height, "Height attribute must be set!");
                break;
            default:
                throw new IllegalArgumentException("Unknown resize strategy " + resizeStrategy + "! Known strategies are \"limit\" or \"crop\".");
        }
        if (width != null) {
            Validate.inclusiveBetween(1, MAX_IMAGE_SIZE, width, "Width can be between 1 and " + MAX_IMAGE_SIZE);
        }
        if (height != null) {
            Validate.inclusiveBetween(1, MAX_IMAGE_SIZE, height, "Height can be between 1 and " + MAX_IMAGE_SIZE);
        }

        Path imagePath = fileStorageManager.convertUrlToPath(url);
        Path resizedImagePath = getResizedImagePath(imagePath, resizeStrategy, width, height);

        String resizedImageUrl = fileStorageManager.createFileIfNotExistsAndBuildUrl(resizedImagePath, outputStream ->
                resizeImage(imagePath, resizeStrategy, width, height, outputStream));
        structureHandler.setAttribute("src", resizedImageUrl);
    }

    private Path getResizedImagePath(Path originalImagePath, ResizeStrategy resizeStrategy, Integer width, Integer height) {
        StringBuilder sizeExtension = new StringBuilder("-");
        sizeExtension.append(resizeStrategy.name());
        sizeExtension.append('-');
        if (width != null) {
            if (height == null) {
                sizeExtension.append('w');
            }
            sizeExtension.append(width);
        }
        if (height != null) {
            if (width != null) {
                sizeExtension.append('x');
            } else {
                sizeExtension.append('h');
            }
            sizeExtension.append(height);
        }

        String pathWithoutExtension = FilenameUtils.removeExtension(originalImagePath.toString());
        String extension = FilenameUtils.getExtension(originalImagePath.toString());

        return fileStorageManager.convertToTempDirectoryPath(Paths.get(pathWithoutExtension + sizeExtension.toString() + '.' + extension));
    }

    private void resizeImage(Path originalImagePath, ResizeStrategy resizeStrategy, Integer newWidth, Integer newHeight, OutputStream outputStream) {
        try {
            BufferedImage originalImage = ImageIO.read(originalImagePath.toFile());
            int originalWidth = originalImage.getWidth(null);
            int originalHeight = originalImage.getHeight(null);

            Image image;
            int width = newWidth != null ? newWidth : MAX_IMAGE_SIZE;
            int height = newHeight != null ? newHeight : MAX_IMAGE_SIZE;

            switch (resizeStrategy) {
                case LIMIT:
                    if (Math.abs(originalWidth - width) > Math.abs(originalHeight - height)) {
                        height = (int) (width * ((float) originalHeight / originalWidth));
                    } else {
                        width = (int) (height * ((float) originalWidth / originalHeight));
                    }
                    image = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    break;
                case CROP:
                    int cropWidth = originalWidth;
                    int cropHeight = originalHeight;
                    int x = 0;
                    int y = 0;
                    if (Math.abs(originalWidth - width) > Math.abs(originalHeight - height)) {
                        cropWidth *= (float) originalHeight / originalWidth;
                        x = (originalWidth - cropWidth) / 2;
                    } else {
                        cropHeight *= (float) originalWidth / originalHeight;
                        y = (originalHeight - cropHeight) / 2;
                    }
                    image = originalImage.getSubimage(x, y, cropWidth, cropHeight).getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown resize strategy " + resizeStrategy);
            }

            log.debug("Resizing image {} from {}x{} to {}x{}", originalImagePath, originalWidth, originalHeight, width, height);

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, null);
            graphics2D.dispose();
            ImageIO.write(bufferedImage, FilenameUtils.getExtension(originalImagePath.toString()), outputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
