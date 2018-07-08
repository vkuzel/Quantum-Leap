package cz.quantumleap.gradle.project;

import org.gradle.api.Project;

/**
 * Spring Boot project is the submodule (sub-project) that contains main class
 * annotated by @SpringBootProject annotation. The Spring Boot gradle plugin is
 * applied on root project so all features are available from root project but
 * main class should be searched in this submodule.
 */
public class SpringBootProject extends SubProject {

    public SpringBootProject(Project project) {
        super(project);
    }
}
