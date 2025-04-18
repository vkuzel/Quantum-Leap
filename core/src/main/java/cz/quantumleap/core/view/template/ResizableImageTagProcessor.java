package cz.quantumleap.core.view.template;

import cz.quantumleap.core.filestorage.FileStorageManager;
import org.apache.commons.io.FilenameUtils;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static cz.quantumleap.core.utils.Strings.isBlank;
import static cz.quantumleap.core.utils.Utils.min;
import static java.util.Objects.requireNonNull;

public class ResizableImageTagProcessor extends AbstractAttributeTagProcessor {

    private enum ResizeStrategy {
        LIMIT, CROP
    }

    public static final Logger log = LoggerFactory.getLogger(ResizableImageTagProcessor.class);

    private static final String ATTR_NAME = "resize";
    private static final int PRECEDENCE = 10000;

    private static final Set<String> SUPPORTED_FORMATS = Set.of("JPG", "JPEG", "PNG", "GIF", "BMP", "WBMP");
    private static final int MAX_IMAGE_SIZE = 3000;

    private final FileStorageManager fileStorageManager;

    ResizableImageTagProcessor(String dialectPrefix, FileStorageManager fileStorageManager) {
        super(TemplateMode.HTML, dialectPrefix, "img", false, ATTR_NAME, true, PRECEDENCE, true);
        this.fileStorageManager = fileStorageManager;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        var resizeStrategy = isBlank(attributeValue) ? ResizeStrategy.LIMIT : ResizeStrategy.valueOf(attributeValue.toUpperCase());
        var url = tag.getAttributeValue("src");
        var width = tag.hasAttribute("width") ? Integer.parseInt(tag.getAttributeValue("width")) : null;
        var height = tag.hasAttribute("height") ? Integer.parseInt(tag.getAttributeValue("height")) : null;

        if (isBlank(url) || !SUPPORTED_FORMATS.contains(FilenameUtils.getExtension(url).toUpperCase())) {
            return;
        }

        switch (resizeStrategy) {
            case LIMIT -> {
                if (width == null && height == null) {
                    throw new IllegalArgumentException("Width or height must be specified!");
                }
            }
            case CROP -> {
                requireNonNull(width, "Width attribute must be set!");
                requireNonNull(height, "Height attribute must be set!");
            }
        }

        if (width != null && (width < 1 || width > MAX_IMAGE_SIZE)) {
            throw new IllegalArgumentException("Width must be between 1 and " + MAX_IMAGE_SIZE);
        }
        if (height != null && (height < 1 || height > MAX_IMAGE_SIZE)) {
            throw new IllegalArgumentException("Height must be between 1 and " + MAX_IMAGE_SIZE);
        }

        var imagePath = fileStorageManager.convertUrlToPath(url);
        if (!Files.exists(imagePath)) {
            return;
        }

        var resizedImagePath = getResizedImagePath(imagePath, resizeStrategy, width, height);

        var resizedImageUrl = fileStorageManager.saveFileIfNotExistsAndBuildUrl(resizedImagePath, outputStream ->
                resizeImage(imagePath, resizeStrategy, width, height, outputStream));
        structureHandler.setAttribute("src", resizedImageUrl);
    }

    private Path getResizedImagePath(Path originalImagePath, ResizeStrategy resizeStrategy, Integer width, Integer height) {
        var sizeExtension = new StringBuilder("-");
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

        var pathWithoutExtension = FilenameUtils.removeExtension(originalImagePath.toString());
        var extension = FilenameUtils.getExtension(originalImagePath.toString());

        return fileStorageManager.convertToTempDirectoryPath(Paths.get(pathWithoutExtension + sizeExtension + '.' + extension));
    }

    private void resizeImage(Path originalImagePath, ResizeStrategy resizeStrategy, Integer newWidth, Integer newHeight, OutputStream outputStream) {
        try {
            var originalImage = ImageIO.read(originalImagePath.toFile());
            var originalWidth = originalImage.getWidth(null);
            var originalHeight = originalImage.getHeight(null);

            var width = min(newWidth, MAX_IMAGE_SIZE);
            var height = min(newHeight, MAX_IMAGE_SIZE);

            var image = switch (resizeStrategy) {
                case LIMIT -> new ResizeLimit(originalWidth, originalHeight, width, height).resize(originalImage);
                case CROP -> new ResizeCrop(originalWidth, originalHeight, width, height).resize(originalImage);
            };

            log.debug("Resizing image {} from {}x{} to {}x{}", originalImagePath, originalWidth, originalHeight, width, height);

            var bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            var graphics2D = bufferedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, null);
            graphics2D.dispose();
            ImageIO.write(bufferedImage, FilenameUtils.getExtension(originalImagePath.toString()), outputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    static class ResizeLimit {

        private final int originalWidth;
        private final int originalHeight;
        private final int width;
        private final int height;
        private int calculatedWidth;
        private int calculatedHeight;

        ResizeLimit(int originalWidth, int originalHeight, int width, int height) {
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
            this.width = width;
            this.height = height;
            calculate();
        }

        private void calculate() {
            calculatedWidth = width;
            calculatedHeight = height;
            if ((float) width / originalWidth * originalHeight <= height) {
                calculatedHeight = (int) (originalHeight * ((float) width / originalWidth));
            } else {
                calculatedWidth = (int) (originalWidth * ((float) height / originalHeight));
            }
        }

        int getCalculatedWidth() {
            return calculatedWidth;
        }

        int getCalculatedHeight() {
            return calculatedHeight;
        }

        private Image resize(BufferedImage image) {
            return image.getScaledInstance(calculatedWidth, calculatedHeight, Image.SCALE_SMOOTH);
        }
    }

    static class ResizeCrop {

        private final int originalWidth;
        private final int originalHeight;
        private final int width;
        private final int height;
        private int cropWidth;
        private int cropHeight;
        private int x;
        private int y;

        ResizeCrop(int originalWidth, int originalHeight, int width, int height) {
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
            this.width = width;
            this.height = height;
            calculate();
        }

        private void calculate() {
            cropWidth = originalWidth;
            cropHeight = originalHeight;
            x = 0;
            y = 0;
            if (originalWidth > originalHeight) {
                cropWidth = originalHeight;
                x = (originalWidth - originalHeight) / 2;
            } else if (originalHeight > originalWidth) {
                cropHeight = originalWidth;
                y = (originalHeight - originalWidth) / 2;
            }
        }

        int getCropWidth() {
            return cropWidth;
        }

        int getCropHeight() {
            return cropHeight;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

        private Image resize(BufferedImage image) {
            return image.getSubimage(x, y, cropWidth, cropHeight).getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }
    }
}
