package cz.quantumleap.gradle.utils;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectUtils {

    public static SourceSetContainer getSourceSets(Project project) {
        return project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets();
    }

    public static String getExtraProperty(Project project, String propertyName, String defaultValue) {
        project.getLogger().debug("Getting extra property " + propertyName + " from project " + project.getName());
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        if (ext.has(propertyName)) {
            Object property = ext.get(propertyName);
            if (property != null) {
                if (property instanceof String) {
                    return (String) property;
                } else {
                    project.getLogger().warn("Extra property " + propertyName + " is not string. It's value will be ignored!");
                }
            } else {
                project.getLogger().debug("Extra property " + propertyName + " is null.");
            }
        } else {
            project.getLogger().debug("Extra property " + propertyName + " is not set.");
        }
        return defaultValue;
    }

    public static void setExtraProperty(Project project, String propertyName, String propertyValue) {
        project.getLogger().debug("Setting extra property " + propertyName + "=" + propertyValue + " to project " + project.getName());
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        if (ext.has(propertyName)) {
            project.getLogger().warn("Extra property " + propertyName + " is already set and it will be overwritten.");
        }

        ext.set(propertyName, propertyValue);
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
