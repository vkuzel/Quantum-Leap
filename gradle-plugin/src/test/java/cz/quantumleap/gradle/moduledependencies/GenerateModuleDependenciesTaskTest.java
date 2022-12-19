package cz.quantumleap.gradle.moduledependencies;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
import cz.quantumleap.gradle.project.SpringBootProject;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateModuleDependenciesTaskTest {

    @Test
    public void allDependenciesAreFound() {
        // given
        Project project = setUpProject();
        GenerateModuleDependenciesTask task = getTask(project);

        // when
        Map<Project, ProjectDependencies> dependenciesMap = new HashMap<>();
        task.findAllDependencies(project, dependenciesMap);

        // then
        ProjectDependencies testProject = getDependencies("testProject", dependenciesMap);
        assertEquals("subproject1", testProject.getDependencies().get(0));

        ProjectDependencies subproject1 = getDependencies("subproject1", dependenciesMap);
        assertEquals("subproject2", subproject1.getDependencies().get(0));

        ProjectDependencies subproject2 = getDependencies("subproject2", dependenciesMap);
        assertTrue(subproject2.getDependencies().isEmpty());
    }

    private GenerateModuleDependenciesTask getTask(Project project) {
        return (GenerateModuleDependenciesTask) project.getTasksByName(ModuleDependenciesConfigurer.GENERATE_MODULE_DEPENDENCIES_TASK_NAME, false)
                .stream().findAny().get();

    }

    private Project setUpProject() {
        Project project = ProjectBuilder.builder().withName("testProject").build();
        Project subproject1 = ProjectBuilder.builder().withName("subproject1").withParent(project).build();
        Project subproject2 = ProjectBuilder.builder().withName("subproject2").withParent(project).build();

        project.getPluginManager().apply(JavaLibraryPlugin.class);
        subproject1.getPluginManager().apply(JavaLibraryPlugin.class);
        subproject2.getPluginManager().apply(JavaLibraryPlugin.class);

        project.getDependencies().add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, subproject1);
        subproject1.getDependencies().add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, subproject2);

        (new ModuleDependenciesConfigurer()).configure(new SpringBootProject(project));

        return project;
    }

    private ProjectDependencies getDependencies(String projectName, Map<Project, ProjectDependencies> dependenciesMap) {
        for (Project project : dependenciesMap.keySet()) {
            if (project.getName().equals(projectName)) {
                return dependenciesMap.get(project);
            }
        }
        throw new IllegalArgumentException("Project " + projectName + " not found!");
    }
}