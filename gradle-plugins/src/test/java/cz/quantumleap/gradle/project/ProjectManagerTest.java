package cz.quantumleap.gradle.project;

import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class ProjectManagerTest {

    @Mock
    private Project project;

    private ProjectManager manager;

    @Before
    public void setUp() {
        manager = new ProjectManager(project);
    }

    @Test
    public void getRootProject() throws Exception {
        // given
        doReturn(project).when(project).getProject();

        // when
        RootProject rootProject = manager.getRootProject();

        // then
        assertEquals(project, rootProject.getProject());
    }

    @Test
    public void getSpringBootProject() throws Exception {
        // given
        Project coreProject = mock(Project.class);
        doReturn(coreProject).when(coreProject).getProject();
        doReturn("core").when(coreProject).getName();
        doReturn(coreProject).when(project).findProject("core");

        // when
        SpringBootProject springBootProject = manager.getSpringBootProject();

        // then
        assertEquals(coreProject, springBootProject.getProject());
    }

    @Test
    public void getAllProjects() throws Exception {
        // given
        Set<Project> projects = Collections.emptySet();
        doReturn(projects).when(project).getAllprojects();

        // when
        Set<Project> allProjects = manager.getAllProjects();

        // then
        assertEquals(projects, allProjects);
    }

    @Test
    public void getSubProjects() throws Exception {
        // given
        Set<SubProject> projects = Collections.emptySet();
        doReturn(projects).when(project).getSubprojects();

        // when
        Set<SubProject> subProjects = manager.getSubProjects();

        // then
        assertEquals(projects, subProjects);
    }
}