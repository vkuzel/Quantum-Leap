package cz.quantumleap.gradle.springboot;

import cz.quantumleap.gradle.project.RootProject;
import cz.quantumleap.gradle.project.SpringBootProject;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.SourceSetOutput;
import org.junit.Test;
import org.springframework.boot.gradle.run.FindMainClassTask;

import java.util.Collections;

import static org.mockito.Mockito.*;

public class SpringBootPluginConfigurerTest {
    @Test
    @SuppressWarnings("unchecked")
    public void configure() throws Exception {
        // given
        RootProject rootProject = mock(RootProject.class);

        PluginContainer pluginContainer = mock(PluginContainer.class);
        doReturn(pluginContainer).when(rootProject).getPlugins();

        FindMainClassTask findMainClassTask = mock(FindMainClassTask.class);
        doReturn(Collections.singleton(findMainClassTask)).when(rootProject).getTasksByName("findMainClass", false);

        SpringBootProject springBootProject = mock(SpringBootProject.class);
        SourceSet sourceSet = mockMainSourceSet(springBootProject);

        SourceSetOutput sourceSetOutput = mock(SourceSetOutput.class);
        doReturn(sourceSetOutput).when(sourceSet).getOutput();

        // when
        SpringBootPluginConfigurer configurer = new SpringBootPluginConfigurer();
        configurer.configure(rootProject, springBootProject);

        // then
        verify(findMainClassTask, times(1)).setMainClassSourceSetOutput(sourceSetOutput);
        verify(findMainClassTask, times(1)).doFirst(any(Action.class));
    }

    private SourceSet mockMainSourceSet(SpringBootProject springBootProject) {
        SourceSet sourceSet = mock(SourceSet.class);

        SourceSetContainer sourceSetContainer = mock(SourceSetContainer.class);
        doReturn(sourceSet).when(sourceSetContainer).getByName("main");

        JavaPluginConvention javaPluginConvention = mock(JavaPluginConvention.class);
        doReturn(sourceSetContainer).when(javaPluginConvention).getSourceSets();

        Convention convention = mock(Convention.class);
        doReturn(javaPluginConvention).when(convention).getPlugin(JavaPluginConvention.class);

        Project project = mock(Project.class);
        doReturn(convention).when(project).getConvention();

        doReturn(project).when(springBootProject).getProject();

        return sourceSet;
    }
}