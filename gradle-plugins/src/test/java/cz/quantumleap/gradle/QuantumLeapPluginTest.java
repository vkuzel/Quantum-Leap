package cz.quantumleap.gradle;

import cz.quantumleap.gradle.jooq.JooqDomainObjectsGeneratorConfigurer;
import cz.quantumleap.gradle.moduledependencies.ModuleDependenciesConfigurer;
import cz.quantumleap.gradle.project.ProjectManager;
import cz.quantumleap.gradle.project.RootProject;
import cz.quantumleap.gradle.project.SpringBootProject;
import cz.quantumleap.gradle.project.SubProject;
import cz.quantumleap.gradle.springboot.SpringBootPluginConfigurer;
import cz.quantumleap.gradle.testfixturessourceset.TestFixturesSourceSetConfigurer;
import cz.quantumleap.gradle.thymeleaf.ThymeleafDependenciesConfigurer;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.gradle.dependencymanagement.DependencyManagementPluginFeatures;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuantumLeapPluginTest {

    @Mock
    private ModuleDependenciesConfigurer moduleDependenciesConfigurer;
    @Mock
    private SpringBootPluginConfigurer springBootPluginConfigurer;
    @Mock
    private TestFixturesSourceSetConfigurer testFixturesSourceSetConfigurer;
    @Mock
    private JooqDomainObjectsGeneratorConfigurer jooqDomainObjectsGeneratorConfigurer;
    @Mock
    private DependencyManagementPluginFeatures dependencyManagementPluginFeatures;
    @Mock
    private ThymeleafDependenciesConfigurer thymeleafDependenciesConfigurer;

    private QuantumLeapPlugin plugin;

    @Before
    public void setUp() {
        plugin = new QuantumLeapPlugin(
                moduleDependenciesConfigurer,
                springBootPluginConfigurer,
                testFixturesSourceSetConfigurer,
                jooqDomainObjectsGeneratorConfigurer,
                dependencyManagementPluginFeatures,
                thymeleafDependenciesConfigurer
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void apply() throws Exception {
        // given
        RootProject rootProject = mock(RootProject.class);
        SpringBootProject springBootProject = mock(SpringBootProject.class);
        SubProject subProject = mock(SubProject.class);
        Project anyProject = mock(Project.class);

        RepositoryHandler repositoryHandler = mock(RepositoryHandler.class);
        doReturn(repositoryHandler).when(anyProject).getRepositories();

        PluginContainer pluginContainer = mock(PluginContainer.class);
        doReturn(pluginContainer).when(subProject).getPlugins();

        ProjectManager projectManager = mock(ProjectManager.class);
        doReturn(springBootProject).when(projectManager).getSpringBootProject();
        doReturn(rootProject).when(projectManager).getRootProject();
        doReturn(Collections.singleton(anyProject)).when(projectManager).getAllProjects();
        doReturn(Collections.singleton(subProject)).when(projectManager).getSubProjects();

        // when
        plugin.apply(projectManager);

        // then
        verify(moduleDependenciesConfigurer, times(1)).configure(springBootProject);
        verify(springBootPluginConfigurer, times(1)).configure(rootProject, springBootProject);
        verify(testFixturesSourceSetConfigurer, times(1)).configure(anyProject);
        verify(jooqDomainObjectsGeneratorConfigurer, times(1)).configure(springBootProject);
        verify(dependencyManagementPluginFeatures, times(1)).apply(subProject.getProject());
        verify(thymeleafDependenciesConfigurer, times(1)).configure(rootProject);

        verify(repositoryHandler, times(1)).mavenCentral();
        verify(repositoryHandler, times(1)).maven(any(Action.class));

        verify(pluginContainer, times(1)).apply(JavaPlugin.class);
    }
}