package cz.quantumleap.core.module;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ModuleDependencies {
    private final String projectPath;
    private final ProjectDependencies projectDependencies;

    ModuleDependencies(Resource dependenciesFile, ProjectDependencies projectDependencies) {
        this.projectDependencies = projectDependencies;
        try {
            String dependenciesFileName = dependenciesFile.getFilename();
            String dependenciesFilePath = dependenciesFile.getURL().getPath();
            projectPath = dependenciesFilePath.substring(0, dependenciesFilePath.lastIndexOf(dependenciesFileName));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getModuleName() {
        return projectDependencies.getName();
    }

    public boolean isInModule(URL resourceUrl) {
        String resourcePath = resourceUrl.getPath();
        if (resourcePath.startsWith(projectPath)) {
            String withoutProjectPath = resourcePath.substring(projectPath.length());
            return !withoutProjectPath.contains(ResourceUtils.JAR_URL_SEPARATOR);
        }
        return false;
    }

    List<String> getDependencies() {
        return projectDependencies.getDependencies();
    }
}
