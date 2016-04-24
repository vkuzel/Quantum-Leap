package cz.quantumleap.server.common;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
import cz.quantumleap.server.autoincrement.IncrementsRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ModuleDependencyManager {

    private static final Logger log = LoggerFactory.getLogger(ModuleDependencyManager.class);

    private static final String PROJECT_DEPENDENCIES_FILE = "/projectDependencies.ser";
    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private final List<Dependencies> independentModulesFirst = new ArrayList<>();
    final Comparator<Dependencies> INDEPENDENT_MODULE_FIRST = (module1, module2) ->
            Integer.compare(independentModulesFirst.indexOf(module1), independentModulesFirst.indexOf(module2));

    public List<Dependencies> getIndependentModulesFirst() {
        return independentModulesFirst;
    }

    public List<String> getModuleNames() {
        return independentModulesFirst.stream()
                .map(Dependencies::getModuleName).collect(Collectors.toList());
    }

    @PostConstruct
    private void loadProjectDependencies() {
        Resource[] dependenciesFiles;
        try {
            dependenciesFiles = resourceResolver.getResources("classpath*:" + PROJECT_DEPENDENCIES_FILE);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        for (Resource dependenciesFile : dependenciesFiles) {
            Dependencies dependencies = deserializeDependencies(dependenciesFile);
            log.info("Registering project module: {}", dependencies.getModuleName());

            independentModulesFirst.remove(dependencies);

            int furtherChildPosition = dependencies.getDependencies().stream()
                    .mapToInt(this::getProjectIndex).max().orElse(-1);

            independentModulesFirst.add(furtherChildPosition + 1, dependencies);
        }
    }

    private int getProjectIndex(String projectName) {
        for (Dependencies dependencies : independentModulesFirst) {
            if (Objects.equals(dependencies.getModuleName(), projectName)) {
                return independentModulesFirst.indexOf(dependencies);
            }
        }
        return -1;
    }

    private Dependencies deserializeDependencies(Resource dependenciesFile) {
        try (
                InputStream inputStream = dependenciesFile.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            return new Dependencies(dependenciesFile, (ProjectDependencies) objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public static class Dependencies {
        private final ProjectDependencies projectDependencies;
        private final String projectPath;

        private Dependencies(Resource dependenciesFile, ProjectDependencies projectDependencies) {
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

        private List<String> getDependencies() {
            return projectDependencies.getDependencies();
        }
    }
}
