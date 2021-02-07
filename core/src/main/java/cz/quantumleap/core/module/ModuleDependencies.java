package cz.quantumleap.core.module;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
import org.apache.commons.lang3.Validate;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

public class ModuleDependencies {
    private final String projectPath;
    private final ProjectDependencies projectDependencies;

    ModuleDependencies(Resource dependenciesFile, ProjectDependencies projectDependencies) {
        this.projectDependencies = projectDependencies;
        String dependenciesFileName = dependenciesFile.getFilename();
        String dependenciesFilePath = resourceToStringPath(dependenciesFile);
        Validate.notNull(dependenciesFileName);
        projectPath = dependenciesFilePath.substring(0, dependenciesFilePath.lastIndexOf(dependenciesFileName));
    }

    public String getModuleName() {
        return projectDependencies.getName();
    }

    public boolean containsResource(Resource resource) {
        String resourcePath = resourceToStringPath(resource);
        if (resourcePath.startsWith(projectPath)) {
            String withoutProjectPath = resourcePath.substring(projectPath.length());
            return !withoutProjectPath.contains(ResourceUtils.JAR_URL_SEPARATOR);
        }
        return false;
    }

    List<String> getDependencies() {
        return projectDependencies.getDependencies();
    }

    private String resourceToStringPath(Resource resource) {
        try {
            String path = resource.getURL().getPath();
            Charset charset = Charset.defaultCharset();
            return URLDecoder.decode(path, charset);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
