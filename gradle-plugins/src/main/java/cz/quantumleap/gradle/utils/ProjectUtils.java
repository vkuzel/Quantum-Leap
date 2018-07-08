package cz.quantumleap.gradle.utils;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginUtils.class);

    public static SourceSetContainer getSourceSets(Project project) {
        JavaPluginConvention plugin = project.getConvention()
                .getPlugin(JavaPluginConvention.class);
        if (plugin != null) {
            return plugin.getSourceSets();
        } else {
            throw new IllegalStateException("Project " + project.getName() + " does not have JavaPlugin applied! It's main source set cannot be found!");
        }
    }

    public static String getExtraProperty(Project project, String propertyName, String defaultValue) {
        LOGGER.debug("Getting extra property " + propertyName + " from project " + project.getName());
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        if (ext != null && ext.has(propertyName)) {
            Object property = ext.get(propertyName);
            if (property != null) {
                if (property instanceof String) {
                    return (String) property;
                } else {
                    LOGGER.warn("Extra property " + propertyName + " is not string. It's value will be ignored!");
                }
            } else {
                LOGGER.debug("Extra property " + propertyName + " is null.");
            }
        } else {
            LOGGER.debug("Extra property " + propertyName + " is not set.");
        }
        return defaultValue;
    }

    public static void setExtraProperty(Project project, String propertyName, String propertyValue) {
        LOGGER.debug("Setting extra property " + propertyName + "=" + propertyValue + " to project " + project.getName());
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        if (ext != null) {
            if (ext.has(propertyName)) {
                LOGGER.warn("Extra property " + propertyName + " is already set and it will be overwritten.");
            }

            ext.set(propertyName, propertyValue);
        } else {
            LOGGER.debug("Extra properties in project " + project.getName() + " are not available.");
        }
    }

    public static File findFileInProjectResources(Project project, String path) {
        return getResourceDirsStream(project)
                .map(f -> new File(f, path))
                .filter(File::exists)
                .findAny()
                .orElseThrow(() -> createResourceFileNotFoundException(project, path));
    }

    private static IllegalArgumentException createResourceFileNotFoundException(Project project, String path) {
        String resourceDirs = getResourceDirsStream(project).map(File::getAbsolutePath).collect(Collectors.joining(", "));
        return new IllegalArgumentException("File " + path + " not found in project resources! Searched dirs " + resourceDirs);
    }

    private static Stream<File> getResourceDirsStream(Project project) {
        return project.getAllprojects().stream()
                .flatMap(p -> getMainSourceSetResourceDirs(p).stream());
    }

    private static Set<File> getMainSourceSetResourceDirs(Project project) {
        SourceSet mainSourceSet = getSourceSets(project).getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        return mainSourceSet.getResources().getSrcDirs();
    }
}
