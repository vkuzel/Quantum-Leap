package cz.quantumleap.core.filestorage;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class FileStorageManager {

    public static final String STORAGE_URL_PREFIX = "/storage/";

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yy-MM");

    @Value("${file.storage.dir}")
    private String fileStorageDirectory;

    public String saveFileAndBuildUrl(String directory, MultipartFile multipartFile) {
        Validate.isTrue(StringUtils.isNotBlank(directory));
        Validate.isTrue(!multipartFile.isEmpty());

        Path filePath = createFilePath(directory, multipartFile);
        ensureDirectoryExists(filePath.getParent());

        try {
            multipartFile.transferTo(filePath.toAbsolutePath().toFile());
            return convertPathToUrl(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("File " + filePath + " cannot be saved!", e);
        }
    }

    public void deleteFileByUrl(String url) {
        Validate.isTrue(StringUtils.isNotBlank(url));

        Path filePath = convertUrlToPath(url);
        if (!Files.exists(filePath)) {
            return;
        }

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Path createFilePath(String directory, MultipartFile multipartFile) {
        String childDirectory = LocalDate.now().format(MONTH_FORMATTER);
        String originalFilename = multipartFile.getOriginalFilename();

        Path filePath = Paths.get(fileStorageDirectory, directory, childDirectory, originalFilename);
        int index = 1;

        while (Files.exists(filePath)) {
            String baseName = FilenameUtils.getBaseName(originalFilename);
            String extension = FilenameUtils.getExtension(originalFilename);
            filePath = filePath.resolveSibling(baseName + index++ + '.' + extension);
        }

        return filePath.normalize();
    }

    private void ensureDirectoryExists(Path directory) {
        File directoryFile = directory.toFile();
        if (!directoryFile.exists()) {
            Validate.isTrue(directoryFile.mkdirs(), "Directory " + directory + " cannot be created!");
        }
    }

    private String convertPathToUrl(Path path) {
        Validate.isTrue(path.startsWith(fileStorageDirectory), "Path " + path + " is not storage path!");
        String pathSuffix = path.toString().substring(fileStorageDirectory.length());
        return STORAGE_URL_PREFIX + pathSuffix;
    }

    private Path convertUrlToPath(String url) {
        Validate.isTrue(url.startsWith(STORAGE_URL_PREFIX), "URL " + url + " is not storage URL!");
        String urlSuffix = url.substring(STORAGE_URL_PREFIX.length());
        return Paths.get(fileStorageDirectory, urlSuffix);
    }

    @PostConstruct
    private void ensureStorageDirectoryExists() {
        ensureDirectoryExists(Paths.get(fileStorageDirectory));
    }
}
