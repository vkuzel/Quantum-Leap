package cz.quantumleap.core.filestorage;

import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

import static cz.quantumleap.core.utils.Strings.isBlank;

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
        if (isBlank(directory)) throw new IllegalArgumentException("directory cannot be empty");
        if (multipartFile.isEmpty()) throw new IllegalArgumentException("multipartFile cannot be empty");

        var filePath = createFilePath(directory, multipartFile);
        ensureDirectoryExists(filePath.getParent());

        try {
            var file = filePath.toAbsolutePath().toFile();
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

        try (var outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
             var bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            supplier.accept(bufferedOutputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized <T> T readFile(Path path, Function<InputStream, T> reader, T defaultValue) {
        if (!Files.exists(path)) {
            return defaultValue;
        }

        try (var inputStream = Files.newInputStream(path, StandardOpenOption.READ);
             var bufferedInputStream = new BufferedInputStream(inputStream)) {
            return reader.apply(bufferedInputStream);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void deleteFileByUrl(String url) {
        if (isBlank(url)) throw new IllegalArgumentException("url cannot be empty");
        var path = convertUrlToPath(url);
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
        if (!path.startsWith(fileStorageDirectory)) {
            throw new IllegalArgumentException("Path " + path + " is not storage path!");
        }
        var pathSuffix = path.toString().substring(fileStorageDirectory.length());
        return STORAGE_URL_PREFIX + pathSuffix;
    }

    public Path convertUrlToPath(String url) {
        if (!url.startsWith(STORAGE_URL_PREFIX)) {
            throw new IllegalArgumentException("Url " + url + " is not storage url!");
        }
        var urlSuffix = url.substring(STORAGE_URL_PREFIX.length());
        return Paths.get(fileStorageDirectory, urlSuffix);
    }

    /**
     * A File stored in temporary directory will be removed 30 days after its creation.
     *
     * @param path A storage path.
     * @return A storage path with TEMP_DIRECTORY.
     */
    public Path convertToTempDirectoryPath(Path path) {
        if (!path.startsWith(fileStorageDirectory)) {
            throw new IllegalArgumentException("Path " + path + " is not storage path!");
        }
        var fileStoragePath = Paths.get(fileStorageDirectory);
        return Paths.get(fileStorageDirectory, TEMP_DIRECTORY, fileStoragePath.relativize(path).toString());
    }

    private Path createFilePath(String directory, MultipartFile multipartFile) { // TODO Resolve
        var childDirectory = LocalDate.now().format(MONTH_FORMATTER);
        var originalFilename = multipartFile.getOriginalFilename();

        var filePath = Paths.get(fileStorageDirectory, directory, childDirectory, originalFilename);
        var index = 1;

        while (Files.exists(filePath)) {
            var baseName = FilenameUtils.getBaseName(originalFilename);
            var extension = FilenameUtils.getExtension(originalFilename);
            filePath = filePath.resolveSibling(baseName + index++ + '.' + extension);
        }

        return filePath.normalize();
    }

    public Path createFilePath(String directory, String fileName) {
        return Paths.get(fileStorageDirectory, directory, fileName);
    }

    @Scheduled(cron = "0 0 3 * * *")
    protected void deleteExpiredTempFiles() {
        var tempDirectoryPath = Paths.get(fileStorageDirectory, TEMP_DIRECTORY);

        if (!Files.exists(tempDirectoryPath)) {
            return;
        }

        var startTime = System.currentTimeMillis();
        var monthAgo = LocalDateTime.now().minusMonths(TEMP_FILE_LIFESPAN_MONTHS);

        try {
            Files.walkFileTree(tempDirectoryPath, new ExpiredTempFilesDeletor(monthAgo));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        log.debug("Temporary files cleared in {} ms", System.currentTimeMillis() - startTime);
    }

    private void ensureDirectoryExists(Path directory) {
        var directoryFile = directory.toFile();
        if (!directoryFile.exists()) {
            var result = directoryFile.mkdirs();
            if (!result) throw new IllegalStateException("Failed to create directory " + directory);
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
            try (var dirStream = Files.newDirectoryStream(directory)) {
                return !dirStream.iterator().hasNext();
            }
        }
    }
}
