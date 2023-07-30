package cz.quantumleap.gradle.project;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskContainer;

import java.util.Set;

public abstract class AbstractProject {

    private final Project project;

    public AbstractProject(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public DependencyHandler getDependencies() {
        return project.getDependencies();
    }

    public Set<Task> getTasksByName(String s, boolean b) {
        return project.getTasksByName(s, b);
    }

    public TaskContainer getTasks() {
        return project.getTasks();
    }

    public PluginContainer getPlugins() {
        return project.getPlugins();
    }
}
