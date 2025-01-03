package cz.quantumleap.core.module;

import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class ModuleDependencies {
    private final String projectPath;
    private final com.github.vkuzel.gradleprojectdependencies.ModuleDependencies moduleDependencies;

    ModuleDependencies(Resource dependenciesFile, com.github.vkuzel.gradleprojectdependencies.ModuleDependencies moduleDependencies) {
        this.moduleDependencies = moduleDependencies;
        var dependenciesFileName = dependenciesFile.getFilename();
        var dependenciesFilePath = resourceToStringPath(dependenciesFile);
        requireNonNull(dependenciesFileName);
        projectPath = dependenciesFilePath.substring(0, dependenciesFilePath.lastIndexOf(dependenciesFileName));
    }

    public String getModuleName() {
        return moduleDependencies.name();
    }

    public boolean containsResource(Resource resource) {
        var resourcePath = resourceToStringPath(resource);
        if (resourcePath.startsWith(projectPath)) {
            var withoutProjectPath = resourcePath.substring(projectPath.length());
            return !withoutProjectPath.contains(ResourceUtils.JAR_URL_SEPARATOR);
        }
        return false;
    }

    List<String> getDependencies() {
        return moduleDependencies.dependencies();
    }

    private String resourceToStringPath(Resource resource) {
        try {
            var path = resource.getURL().getPath();
            var charset = Charset.defaultCharset();
            return URLDecoder.decode(path, charset);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
