package cz.quantumleap.gradle.project;

import org.gradle.api.Project;

import java.util.Set;
import java.util.stream.Collectors;

public class ProjectManager {

    private static final String SPRING_BOOT_PROJECT_NAME = "core";

    private final Project rootProject;

    public ProjectManager(Project rootProject) {
        if (rootProject.getParent() != null) {
            throw new IllegalArgumentException("Project " + rootProject.getName() + " must be root project!");
        }
        this.rootProject = rootProject;
    }

    public RootProject getRootProject() {
        return new RootProject(rootProject);
    }

    public SpringBootProject getSpringBootProject() {
        Project project = rootProject.findProject(SPRING_BOOT_PROJECT_NAME);
        if (project == null) {
            throw new IllegalStateException("Spring Boot project 'core' was not found!");
        } else if (project.getName().equals(rootProject.getName())) {
            throw new IllegalStateException("Spring Boot project and root project is the same!");
        }
        return new SpringBootProject(project);
    }

    public Set<Project> getAllProjects() {
        return rootProject.getAllprojects();
    }

    public Set<SubProject> getSubProjects() {
        return rootProject.getSubprojects().stream()
                .map(SubProject::new)
                .collect(Collectors.toSet());
    }
}
