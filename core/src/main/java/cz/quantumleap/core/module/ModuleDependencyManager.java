package cz.quantumleap.core.module;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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

    private final List<ModuleDependencies> independentModulesFirst = new ArrayList<>();

    public List<ModuleDependencies> getIndependentModulesFirst() {
        return independentModulesFirst;
    }

    public List<String> getModuleNames() {
        return independentModulesFirst.stream()
                .map(ModuleDependencies::getModuleName).collect(Collectors.toList());
    }

    public Comparator<ModuleDependencies> independentModuleFirst() {
        return Comparator.comparingInt(independentModulesFirst::indexOf);
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
            ModuleDependencies moduleDependencies = deserializeDependencies(dependenciesFile);
            log.info("Registering project module: {}", moduleDependencies.getModuleName());

            independentModulesFirst.remove(moduleDependencies);

            int furtherChildPosition = moduleDependencies.getDependencies().stream()
                    .mapToInt(this::getProjectIndex).max().orElse(-1);

            independentModulesFirst.add(furtherChildPosition + 1, moduleDependencies);
        }
    }

    private int getProjectIndex(String projectName) {
        for (ModuleDependencies moduleDependencies : independentModulesFirst) {
            if (Objects.equals(moduleDependencies.getModuleName(), projectName)) {
                return independentModulesFirst.indexOf(moduleDependencies);
            }
        }
        return -1;
    }

    private ModuleDependencies deserializeDependencies(Resource dependenciesFile) {
        try (
                InputStream inputStream = dependenciesFile.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            return new ModuleDependencies(dependenciesFile, (ProjectDependencies) objectInputStream.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
