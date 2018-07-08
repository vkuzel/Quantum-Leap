package cz.quantumleap.gradle;

import cz.quantumleap.gradle.jooq.JooqDomainObjectsGeneratorConfigurer;
import cz.quantumleap.gradle.moduledependencies.ModuleDependenciesConfigurer;
import cz.quantumleap.gradle.project.ProjectManager;
import cz.quantumleap.gradle.project.SubProject;
import cz.quantumleap.gradle.springboot.SpringBootPluginConfigurer;
import cz.quantumleap.gradle.testfixturessourceset.TestFixturesSourceSetConfigurer;
import cz.quantumleap.gradle.thymeleaf.ThymeleafDependenciesConfigurer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.springframework.boot.gradle.dependencymanagement.DependencyManagementPluginFeatures;

import javax.inject.Inject;

public class QuantumLeapPlugin implements Plugin<Project> {

    private static final String JITPACK_REPOSITORY = "https://jitpack.io";

    private final ModuleDependenciesConfigurer moduleDependenciesConfigurer;
    private final SpringBootPluginConfigurer springBootPluginConfigurer;
    private final TestFixturesSourceSetConfigurer testFixturesSourceSetConfigurer;
    private final JooqDomainObjectsGeneratorConfigurer jooqDomainObjectsGeneratorConfigurer;
    private final DependencyManagementPluginFeatures dependencyManagementPluginFeatures;
    private final ThymeleafDependenciesConfigurer thymeleafDependenciesConfigurer;

    @Inject
    public QuantumLeapPlugin() {
        this(
                new ModuleDependenciesConfigurer(),
                new SpringBootPluginConfigurer(),
                new TestFixturesSourceSetConfigurer(),
                new JooqDomainObjectsGeneratorConfigurer(),
                new DependencyManagementPluginFeatures(),
                new ThymeleafDependenciesConfigurer()
        );
    }

    QuantumLeapPlugin(
            ModuleDependenciesConfigurer moduleDependenciesConfigurer,
            SpringBootPluginConfigurer springBootPluginConfigurer,
            TestFixturesSourceSetConfigurer testFixturesSourceSetConfigurer,
            JooqDomainObjectsGeneratorConfigurer jooqDomainObjectsGeneratorConfigurer,
            DependencyManagementPluginFeatures dependencyManagementPluginFeatures,
            ThymeleafDependenciesConfigurer thymeleafDependenciesConfigurer
    ) {
        this.moduleDependenciesConfigurer = moduleDependenciesConfigurer;
        this.springBootPluginConfigurer = springBootPluginConfigurer;
        this.testFixturesSourceSetConfigurer = testFixturesSourceSetConfigurer;
        this.jooqDomainObjectsGeneratorConfigurer = jooqDomainObjectsGeneratorConfigurer;
        this.dependencyManagementPluginFeatures = dependencyManagementPluginFeatures;
        this.thymeleafDependenciesConfigurer = thymeleafDependenciesConfigurer;
    }

    @Override
    public void apply(Project project) {
        System.out.println("applied original!");
        ProjectManager projectManager = new ProjectManager(project);

        apply(projectManager);
    }

    void apply(ProjectManager projectManager) {
        projectManager.getAllProjects().forEach(this::configureStandardRepositories);

        projectManager.getSubProjects().forEach(this::configureStandardPlugins);

        moduleDependenciesConfigurer.configure(projectManager.getSpringBootProject());

        springBootPluginConfigurer.configure(projectManager.getRootProject(), projectManager.getSpringBootProject());

        projectManager.getAllProjects().forEach(testFixturesSourceSetConfigurer::configure);

        jooqDomainObjectsGeneratorConfigurer.configure(projectManager.getSpringBootProject());

        thymeleafDependenciesConfigurer.configure(projectManager.getRootProject());
    }

    private void configureStandardRepositories(Project project) {
        project.getRepositories().mavenCentral();
        project.getRepositories().maven(mavenArtifactRepository -> mavenArtifactRepository.setUrl(JITPACK_REPOSITORY));
    }

    private void configureStandardPlugins(SubProject subProject) {
        dependencyManagementPluginFeatures.apply(subProject.getProject());
        subProject.getPlugins().apply(JavaPlugin.class);
    }
}
