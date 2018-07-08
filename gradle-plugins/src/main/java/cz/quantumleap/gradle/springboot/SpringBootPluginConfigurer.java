package cz.quantumleap.gradle.springboot;

import cz.quantumleap.gradle.project.RootProject;
import cz.quantumleap.gradle.project.SpringBootProject;
import cz.quantumleap.gradle.utils.ProjectUtils;
import org.gradle.api.tasks.SourceSet;
import org.springframework.boot.gradle.plugin.SpringBootPlugin;
import org.springframework.boot.gradle.run.FindMainClassTask;

public class SpringBootPluginConfigurer {

    private static final String FIND_MAIN_CLASS_TASK_NAME = "findMainClass";
    private static final String MAIN_CLASS_NAME_PROPERTY = "mainClassName";

    public void configure(RootProject rootProject, SpringBootProject springBootProject) {
        rootProject.getPlugins().apply(SpringBootPlugin.class);

        // SpringBoot plugin is applied on rootProject but we need to search
        // for mainClass in springBootProject. So find mainClass task's source
        // set is going to be changed to springBootProject's source set.
        rootProject.getTasksByName(FIND_MAIN_CLASS_TASK_NAME, false).forEach(task ->
                configureFindMainClassTask(rootProject, springBootProject, (FindMainClassTask) task)
        );
    }

    private void configureFindMainClassTask(RootProject rootProject, SpringBootProject springBootProject, FindMainClassTask task) {
        // Java plugin has to be applied on core-project before this method is called!
        SourceSet mainSourceSet = ProjectUtils.getSourceSets(springBootProject.getProject()).getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        task.setMainClassSourceSetOutput(mainSourceSet.getOutput());

        task.doFirst(t -> {
            // MainClass property configured in springBootProject will be copied
            // to rootProject because rootProject does have SpringBoot plugin
            // applied.
            String mainClass = ProjectUtils.getExtraProperty(springBootProject.getProject(), MAIN_CLASS_NAME_PROPERTY, null);
            if (mainClass != null) {
                ProjectUtils.setExtraProperty(rootProject.getProject(), MAIN_CLASS_NAME_PROPERTY, mainClass);
            }
        });
    }
}
