package cz.quantumleap.gradle.project;

import org.gradle.api.Project;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ProjectManagerTest {

    @Test
    public void singleProjectIsRootProject() {
        // when
        Project project = Mockito.mock(Project.class);
        ProjectManager manager = new ProjectManager(project);
        RootProject rootProject = manager.getRootProject();

        // then
        assertEquals(project, rootProject.getProject());
    }

    @Test
    public void springBootProjectIsFound() {
        // given
        Project project = Mockito.mock(Project.class);
        ProjectManager manager = new ProjectManager(project);
        Project coreProject = mock(Project.class);
        doReturn("core").when(coreProject).getName();
        doReturn(coreProject).when(project).findProject("core");

        // when
        SpringBootProject springBootProject = manager.getSpringBootProject();

        // then
        assertEquals(coreProject, springBootProject.getProject());
    }

    @Test
    public void allProjectsAreReturned() {
        // given
        Project project = Mockito.mock(Project.class);
        ProjectManager manager = new ProjectManager(project);
        Set<Project> projects = Collections.emptySet();
        doReturn(projects).when(project).getAllprojects();

        // when
        Set<Project> allProjects = manager.getAllProjects();

        // then
        assertEquals(projects, allProjects);
    }

    @Test
    public void subProjectsAreReturned() {
        // given
        Project project = Mockito.mock(Project.class);
        ProjectManager manager = new ProjectManager(project);
        Set<SubProject> projects = Collections.emptySet();
        doReturn(projects).when(project).getSubprojects();

        // when
        Set<SubProject> subProjects = manager.getSubProjects();

        // then
        assertEquals(projects, subProjects);
    }
}