package cz.quantumleap.gradle.springboot;

import cz.quantumleap.gradle.project.RootProject;
import cz.quantumleap.gradle.project.SpringBootProject;
import cz.quantumleap.gradle.utils.ProjectUtils;
import org.gradle.api.Task;
import org.gradle.api.internal.provider.DefaultProvider;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.SourceSet;
import org.gradle.process.JavaExecSpec;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;
import org.springframework.boot.gradle.tasks.bundling.BootArchive;
import org.springframework.boot.loader.tools.MainClassFinder;

import java.io.IOException;

import static org.springframework.boot.gradle.plugin.SpringBootPlugin.BOOT_JAR_TASK_NAME;
import static org.springframework.boot.gradle.plugin.SpringBootPlugin.BOOT_WAR_TASK_NAME;

public class SpringBootPluginConfigurer {

    // Boot run task name is specified in JavaPluginAction as literal.
    private static final String BOOT_RUN_TASK_NAME = "bootRun";
    private static final String MAIN_CLASS_PROPERTY_NAME = "mainClass";

    public void configure(RootProject rootProject, SpringBootProject springBootProject) {
        rootProject.getPlugins().apply(SpringBootPlugin.class);

        // SpringBoot plugin is applied on rootProject but we need to search
        // for mainClass in a springBootProject. To do this we have to override
        // MainClassConventions of Spring Boot Gradle plugin by setting correct
        // mainClassName project property in our own implementation.
        rootProject.getTasksByName(BOOT_JAR_TASK_NAME, false).forEach(task ->
                setMainClassConvention(task, rootProject, springBootProject));
        rootProject.getTasksByName(BOOT_WAR_TASK_NAME, false).forEach(task ->
                setMainClassConvention(task, rootProject, springBootProject));
        rootProject.getTasksByName(BOOT_RUN_TASK_NAME, false).forEach(task ->
                setMainClassConvention(task, rootProject, springBootProject));
    }

    private void setMainClassConvention(Task task, RootProject rootProject, SpringBootProject springBootProject) {
        Provider<String> mainClassProvider = new DefaultProvider<>(() -> findMainClass(rootProject, springBootProject));
        if (task instanceof BootArchive) {
            ((BootArchive) task).getMainClass().convention(mainClassProvider);
        } else if (task instanceof JavaExecSpec) {
            ((JavaExecSpec) task).getMainClass().convention(mainClassProvider);
        } else {
            throw new IllegalArgumentException("Unknown task type " + task.getClass());
        }
    }

    private String findMainClass(RootProject rootProject, SpringBootProject springBootProject) {
        String mainClass = ProjectUtils.getExtraProperty(rootProject.getProject(), MAIN_CLASS_PROPERTY_NAME, null);
        if (mainClass != null) {
            return mainClass;
        }

        mainClass = ProjectUtils.getExtraProperty(springBootProject.getProject(), MAIN_CLASS_PROPERTY_NAME, null);
        if (mainClass != null) {
            return mainClass;
        }

        try {
            SourceSet mainSourceSet = ProjectUtils.getSourceSets(springBootProject.getProject()).getByName(SourceSet.MAIN_SOURCE_SET_NAME);
            return MainClassFinder.findMainClass(mainSourceSet.getOutput().getSingleFile());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
