package cz.quantumleap.gradle.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PluginUtils {

    public static void ensureDirectoryExists(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new IllegalStateException("Directory " + directory.toString() + " cannot be created!", e);
            }
        }
    }
}
