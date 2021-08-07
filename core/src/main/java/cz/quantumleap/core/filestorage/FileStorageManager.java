package cz.quantumleap.core.filestorage;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class FileStorageManager {

    private static final Logger log = LoggerFactory.getLogger(FileStorageManager.class);

    public static final String STORAGE_URL_PREFIX = "/storage/";

    private static final String TEMP_DIRECTORY = "/tmp/";
    private static final int TEMP_FILE_LIFESPAN_MONTHS = 1;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yy-MM");

    private final String fileStorageDirectory;

    public FileStorageManager(@Value("${quantumleap.file.storage.dir}") String fileStorageDirectory) {
        this.fileStorageDirectory = fileStorageDirectory;
    }

    public String saveMultipartFileAndBuildUrl(String directory, MultipartFile multipartFile) {
        Validate.isTrue(StringUtils.isNotBlank(directory));
        Validate.isTrue(!multipartFile.isEmpty());

        Path filePath = createFilePath(directory, multipartFile);
        ensureDirectoryExists(filePath.getParent());

        try {
            File file = filePath.toAbsolutePath().toFile();
            log.debug("Saving multipart file {} as {}", multipartFile.getName(), file);
            multipartFile.transferTo(file);
            return convertPathToUrl(filePath);
        } catch (IOException e) {
            throw new IllegalStateException("File " + filePath + " cannot be saved!", e);
        }
    }

    public String saveFileIfNotExistsAndBuildUrl(Path path, Consumer<OutputStream> supplier) {
        saveFileIfNotExists(path, supplier);
        return convertPathToUrl(path);
    }

    public synchronized void saveFileIfNotExists(Path path, Consumer<OutputStream> supplier) {
        if (Files.exists(path)) {
            return;
        }

        saveFile(path, supplier);
    }

    public synchronized void saveFile(Path path, Consumer<OutputStream> supplier) {
        ensureDirectoryExists(path.getParent());
        log.debug("Creating file {}", path);

        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            supplier.accept(bufferedOutputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized <T> T readFile(Path path, Function<InputStream, T> reader, T defaultValue) {
        if (!Files.exists(path)) {
            return defaultValue;
        }

        try (InputStream inputStream = Files.newInputStream(path, StandardOpenOption.READ);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            return reader.apply(bufferedInputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void deleteFileByUrl(String url) {
        Validate.isTrue(StringUtils.isNotBlank(url));
        Path path = convertUrlToPath(url);
        deleteFile(path);
    }

    public void deleteFile(Path path) {
        if (!Files.exists(path)) {
            return;
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public String convertPathToUrl(Path path) {
        Validate.isTrue(path.startsWith(fileStorageDirectory), "Path " + path + " is not storage path!");
        String pathSuffix = path.toString().substring(fileStorageDirectory.length());
        return STORAGE_URL_PREFIX + pathSuffix;
    }

    public Path convertUrlToPath(String url) {
        Validate.isTrue(url.startsWith(STORAGE_URL_PREFIX), "URL " + url + " is not storage URL!");
        String urlSuffix = url.substring(STORAGE_URL_PREFIX.length());
        return Paths.get(fileStorageDirectory, urlSuffix);
    }

    /**
     * A File stored in temporary directory will be removed 30 days after its creation.
     *
     * @param path A storage path.
     * @return A storage path with TEMP_DIRECTORY.
     */
    public Path convertToTempDirectoryPath(Path path) {
        Validate.isTrue(path.startsWith(fileStorageDirectory), "Path " + path + " is not storage path!");
        Path fileStoragePath = Paths.get(fileStorageDirectory);
        return Paths.get(fileStorageDirectory, TEMP_DIRECTORY, fileStoragePath.relativize(path).toString());
    }

    private Path createFilePath(String directory, MultipartFile multipartFile) { // TODO Resolve
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

    public Path createFilePath(String directory, String fileName) {
        return Paths.get(fileStorageDirectory, directory, fileName);
    }

    @Scheduled(cron = "0 0 3 * * *")
    protected void deleteExpiredTempFiles() {
        Path tempDirectoryPath = Paths.get(fileStorageDirectory, TEMP_DIRECTORY);

        if (!Files.exists(tempDirectoryPath)) {
            return;
        }

        long startTime = System.currentTimeMillis();
        LocalDateTime monthAgo = LocalDateTime.now().minusMonths(TEMP_FILE_LIFESPAN_MONTHS);

        try {
            Files.walkFileTree(tempDirectoryPath, new ExpiredTempFilesDeletor(monthAgo));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        log.debug("Temporary files cleared in {} ms", System.currentTimeMillis() - startTime);
    }

    private void ensureDirectoryExists(Path directory) {
        File directoryFile = directory.toFile();
        if (!directoryFile.exists()) {
            Validate.isTrue(directoryFile.mkdirs(), "Directory " + directory + " cannot be created!");
        }
    }

    @PostConstruct
    private void ensureStorageDirectoryExists() {
        ensureDirectoryExists(Paths.get(fileStorageDirectory));
    }

    private static class ExpiredTempFilesDeletor extends SimpleFileVisitor<Path> {

        private final FileTime keepFilesThreshold;

        private ExpiredTempFilesDeletor(LocalDateTime keepFilesThreshold) {
            this.keepFilesThreshold = FileTime.from(keepFilesThreshold.toInstant(ZoneOffset.UTC));
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (attrs.creationTime().compareTo(keepFilesThreshold) < 0) {
                Files.delete(file);
            }
            return super.visitFile(file, attrs);
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (isDirEmpty(dir)) {
                Files.delete(dir);
            }
            return super.postVisitDirectory(dir, exc);
        }

        private boolean isDirEmpty(final Path directory) throws IOException {
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
                return !dirStream.iterator().hasNext();
            }
        }
    }
}
