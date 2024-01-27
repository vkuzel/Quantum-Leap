package cz.quantumleap.gradle;

import cz.quantumleap.gradle.jooq.JooqDomainObjectsGeneratorConfigurer;
import cz.quantumleap.gradle.moduledependencies.ModuleDependenciesConfigurer;
import cz.quantumleap.gradle.project.ProjectManager;
import cz.quantumleap.gradle.springboot.SpringBootPluginConfigurer;
import io.spring.gradle.dependencymanagement.DependencyManagementPlugin;
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension;
import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.testing.Test;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;

@SuppressWarnings("unused")
public class QuantumLeapPlugin implements Plugin<Project> {

    private static final String SPRING_BOOT_BOM = "org.springframework.boot:spring-boot-dependencies:" + SpringBootPlugin.class.getPackage().getImplementationVersion();
    private static final String TEST_TASK_NAME = "test";

    private final ModuleDependenciesConfigurer moduleDependenciesConfigurer = new ModuleDependenciesConfigurer();
    private final SpringBootPluginConfigurer springBootPluginConfigurer = new SpringBootPluginConfigurer();
    private final JooqDomainObjectsGeneratorConfigurer jooqDomainObjectsGeneratorConfigurer = new JooqDomainObjectsGeneratorConfigurer();

    @Override
    public void apply(Project project) {
        var projectManager = new ProjectManager(project);
        apply(projectManager);
    }

    private void apply(ProjectManager projectManager) {
        projectManager.getAllProjects().forEach(this::configureStandardRepositoriesAndPlugins);
        springBootPluginConfigurer.configure(projectManager.getRootProject(), projectManager.getSpringBootProject());
        moduleDependenciesConfigurer.configure(projectManager.getSpringBootProject());
        jooqDomainObjectsGeneratorConfigurer.configure(projectManager.getSpringBootProject());
    }

    private void configureStandardRepositoriesAndPlugins(Project project) {
        project.getPlugins().apply(JavaLibraryPlugin.class);
        var javaPluginExtension = project.getExtensions().getByType(JavaPluginExtension.class);
        javaPluginExtension.setSourceCompatibility(JavaVersion.VERSION_21);
        javaPluginExtension.setTargetCompatibility(JavaVersion.VERSION_21);
        project.getPlugins().apply(DependencyManagementPlugin.class);
        project.getExtensions().getByType(DependencyManagementExtension.class)
                .imports(importsHandler -> importsHandler.mavenBom(SPRING_BOOT_BOM));
        project.getTasksByName("test", false).forEach(this::applyJUnitPlatform);
    }

    private void applyJUnitPlatform(Task task) {
        if (task instanceof Test) {
            ((Test) task).useJUnitPlatform();
        }
    }
}
