package cz.quantumleap.gradle.moduledependencies;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
import cz.quantumleap.gradle.project.SpringBootProject;

public class ModuleDependenciesConfigurer {

    static final String GENERATE_MODULE_DEPENDENCIES_TASK_NAME = "generateModuleDependencies";

    public void configure(SpringBootProject springBootProject) {
        GenerateModuleDependenciesTask generate = springBootProject.getTasks().create(GENERATE_MODULE_DEPENDENCIES_TASK_NAME, GenerateModuleDependenciesTask.class);
        describeTask(generate);
    }

    private void describeTask(GenerateModuleDependenciesTask task) {
        String description = "Discovers dependencies between project modules and serializes them into a file.";
        description += " Location of the file is project's resource dir/moduleDependencies.ser.";
        description += " Serialized file is of type " + ProjectDependencies.class.getCanonicalName() + ".";
        description += " The type class can be found in project https://github.com/vkuzel/Gradle-Project-Dependencies";
        task.setDescription(description);
        task.setGroup("build");
    }
}
