package cz.quantumleap.gradle.moduledependencies;

import com.github.vkuzel.gradleprojectdependencies.ModuleDependencies;
import cz.quantumleap.gradle.utils.PluginUtils;
import cz.quantumleap.gradle.utils.ProjectUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.result.ResolutionResult;
import org.gradle.api.artifacts.result.ResolvedComponentResult;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;

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

import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME;

public class GenerateModuleDependenciesTask extends DefaultTask {

    private static final String PROJECT_DEPENDENCIES_PATH = "projectDependencies.ser";

    @TaskAction
    public void generate() {
        Project rootProject = getProject().getRootProject();
        Map<Project, ModuleDependencies> dependenciesMap = new HashMap<>();
        findAllDependencies(rootProject, dependenciesMap);

        dependenciesMap.forEach(this::save);
    }

    void findAllDependencies(Project project, Map<Project, ModuleDependencies> dependenciesMap) {
        Configuration compileConfiguration = project.getConfigurations().getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME);
        ResolutionResult result = compileConfiguration.getIncoming().getResolutionResult();
        ResolvedComponentResult componentResult = result.getRoot();

        List<Project> children = componentResult.getDependencies().stream()
                .filter(ResolvedDependencyResult.class::isInstance)
                .map(dr -> ((ResolvedDependencyResult) dr).getSelected())
                .filter(cr -> cr.getId() instanceof ProjectComponentIdentifier)
                .map(cr -> {
                    ProjectComponentIdentifier projectIdentifier = (ProjectComponentIdentifier) cr.getId();
                    return project.findProject(projectIdentifier.getProjectPath());
                })
                .collect(Collectors.toList());

        ModuleDependencies moduleDependencies = createModuleDependencies(project, children);
        dependenciesMap.put(project, moduleDependencies);
        project.getLogger().debug("Discovered module dependencies: {}", moduleDependencies);

        for (Project child : children) {
            if (!dependenciesMap.containsKey(child)) {
                findAllDependencies(child, dependenciesMap);
            }
        }
    }

    private ModuleDependencies createModuleDependencies(Project project, List<Project> children) {
        return new ModuleDependencies(
                project.getName(),
                project.getProjectDir().getName(),
                project.getDepth() == 0,
                children.stream().map(Project::getName).collect(Collectors.toList())
        );
    }

    private void save(Project project, ModuleDependencies moduleDependencies) {
        Path path = getResourcesDir(project).resolve(PROJECT_DEPENDENCIES_PATH);
        PluginUtils.ensureDirectoryExists(path.getParent());
        project.getLogger().info("Serialized module dependencies will be stored in {}", path);

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
        SourceSet mainSourceSet = ProjectUtils.getSourceSets(project).getByName(MAIN_SOURCE_SET_NAME);
        File resourcesDir = null;
        for (File dir : mainSourceSet.getResources().getSrcDirs()) {
            if (resourcesDir != null) {
                project.getLogger().warn("Project {} has more than one resource dirs! This {} will be used to store serialized module dependencies.",
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
