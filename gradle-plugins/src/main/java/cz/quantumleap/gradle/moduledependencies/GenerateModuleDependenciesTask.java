package cz.quantumleap.gradle.moduledependencies;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
import cz.quantumleap.gradle.utils.PluginUtils;
import cz.quantumleap.gradle.utils.ProjectUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.result.ResolutionResult;
import org.gradle.api.artifacts.result.ResolvedComponentResult;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenerateModuleDependenciesTask extends DefaultTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateModuleDependenciesTask.class);

    private static final String PROJECT_CONFIGURATION_COMPILE = "compile";

    private static final String PROJECT_DEPENDENCIES_PATH = "projectDependencies.ser";

    @TaskAction
    public void generate() {
        Project rootProject = getProject().getRootProject();
        Map<Project, ProjectDependencies> dependenciesMap = new HashMap<>();
        findAllDependencies(rootProject, dependenciesMap);

        dependenciesMap.forEach(this::save);
    }

    void findAllDependencies(Project project, Map<Project, ProjectDependencies> dependenciesMap) {
        Configuration compileConfiguration = project.getConfigurations().getByName(PROJECT_CONFIGURATION_COMPILE);
        ResolutionResult result = compileConfiguration.getIncoming().getResolutionResult();
        ResolvedComponentResult componentResult = result.getRoot();

        List<Project> children = componentResult.getDependencies().stream()
                .filter(ResolvedDependencyResult.class::isInstance)
                .map(dr -> ((ResolvedDependencyResult) dr).getSelected())
                .filter(cr -> ProjectComponentIdentifier.class.isInstance(cr.getId()))
                .map(cr -> {
                    ProjectComponentIdentifier projectIdentifier = (ProjectComponentIdentifier) cr.getId();
                    return project.findProject(projectIdentifier.getProjectPath());
                })
                .collect(Collectors.toList());

        ProjectDependencies moduleDependencies = createModuleDependencies(project, children);
        dependenciesMap.put(project, moduleDependencies);
        LOGGER.debug("Discovered module dependencies: {}", moduleDependencies.toString());

        children.forEach(child -> {
            if (!dependenciesMap.containsKey(child)) {
                findAllDependencies(child, dependenciesMap);
            }
        });
    }

    private ProjectDependencies createModuleDependencies(Project project, List<Project> children) {
        return new ProjectDependencies(
                project.getName(),
                project.getProjectDir().getName(),
                project.getDepth() == 0,
                children.stream().map(Project::getName).collect(Collectors.toList())
        );
    }

    private void save(Project project, ProjectDependencies moduleDependencies) {
        Path path = getResourcesDir(project).resolve(PROJECT_DEPENDENCIES_PATH);
        PluginUtils.ensureDirectoryExists(path.getParent());
        LOGGER.info("Serialized module dependencies will be stored in {}", path);

        try (
                OutputStream outputStream = Files.newOutputStream(path);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            objectOutputStream.writeObject(moduleDependencies);
        } catch (IOException e) {
            throw new IllegalStateException("Module dependencies cannot be written into file " + path.toAbsolutePath(), e);
        }
    }

    private Path getResourcesDir(Project project) {
        SourceSet mainSourceSet = ProjectUtils.getSourceSets(project).getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        File resourcesDir = null;
        for (File dir : mainSourceSet.getResources().getSrcDirs()) {
            if (resourcesDir != null) {
                LOGGER.warn("Project {} has more than one resource dirs! This {} will be used to store serialized module dependencies.",
                        project.getName(), resourcesDir.getAbsolutePath());
                break;
            }
            resourcesDir = dir;
        }
        if (resourcesDir == null) {
            throw new IllegalStateException("Resource dir not found in module " + project.getName());
        }
        return resourcesDir.toPath();
    }
}
