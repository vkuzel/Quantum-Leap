package cz.quantumleap.gradle.testfixturessourceset;

import cz.quantumleap.gradle.utils.ProjectUtils;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestFixturesSourceSetConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestFixturesSourceSetConfigurer.class);

    private static final String OUTPUT_DIRS_COMPILE_CONFIGURATION_NAME = "outputDirs";

    private static final String TEST_FIXTURES_COMPILE_CONFIGURATION_NAME = "testFixturesCompile";
    private static final String TEST_FIXTURES_RUNTIME_CONFIGURATION_NAME = "testFixturesRuntime";

    private static final String TEST_FIXTURES_USAGE_COMPILE_CONFIGURATION_NAME = "testFixturesUsageCompile";
    private static final String TEST_FIXTURES_USAGE_RUNTIME_CONFIGURATION_NAME = "testFixturesUsageRuntime";

    private static final String TEST_FIXTURES_SOURCE_SET_PATH = "src/testFixtures";
    private static final String TEST_FIXTURES_SOURCE_SET_NAME = "testFixtures";

    public void configure(Project project) {
        if (project.file(TEST_FIXTURES_SOURCE_SET_PATH).exists()) {
            LOGGER.info("Configuring testFixtures for project {}", project.getName());

            ConfigurationContainer configurations = project.getConfigurations();

            Configuration outputDirsConfiguration = configurations.create(OUTPUT_DIRS_COMPILE_CONFIGURATION_NAME);

            Configuration compileConfiguration = configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME);
            Configuration runtimeConfiguration = configurations.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME);

            // Create testFixturesCompile and testFixtureRuntime configurations so compile and runtime dependencies can
            // be declared in current project's build script.
            // Example: dependencies { testFixturesCompile 'junit:junit:4.12' }
            Configuration compileTestFixturesConfiguration = configurations.create(TEST_FIXTURES_COMPILE_CONFIGURATION_NAME)
                    .extendsFrom(compileConfiguration);
            Configuration runtimeTestFixturesConfiguration = configurations.create(TEST_FIXTURES_RUNTIME_CONFIGURATION_NAME)
                    .extendsFrom(runtimeConfiguration, compileTestFixturesConfiguration);

            // Create testFixturesUsage... configurations so other projects can declare their dependencies to current
            // project's testFixtures. This configuration contains dependencies of testFixtures... configurations
            // together with compiled testFixtures classes.
            Configuration compileTestFixturesUsageConfiguration = configurations.create(TEST_FIXTURES_USAGE_COMPILE_CONFIGURATION_NAME)
                    .extendsFrom(compileTestFixturesConfiguration, outputDirsConfiguration);
            Configuration runtimeTestFixturesUsageConfiguration = configurations.create(TEST_FIXTURES_USAGE_RUNTIME_CONFIGURATION_NAME)
                    .extendsFrom(runtimeTestFixturesConfiguration, compileTestFixturesUsageConfiguration);

            // Add testFixtures to the project's test.
            configurations.getByName(JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME).extendsFrom(compileTestFixturesUsageConfiguration);
            configurations.getByName(JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME).extendsFrom(runtimeTestFixturesUsageConfiguration);

            SourceSet mainSourceSet = ProjectUtils.getSourceSets(project).getByName(SourceSet.MAIN_SOURCE_SET_NAME);

            SourceSet testFixturesSourceSet = ProjectUtils.getSourceSets(project).create(TEST_FIXTURES_SOURCE_SET_NAME);
            // Add configuration to testFixtures source set so dependencies can be used while compiling or running
            // application.
            testFixturesSourceSet.setCompileClasspath(project.files(mainSourceSet.getOutput(), compileTestFixturesConfiguration));
            testFixturesSourceSet.setRuntimeClasspath(project.files(testFixturesSourceSet.getOutput(), testFixturesSourceSet.getCompileClasspath(), runtimeTestFixturesConfiguration));

            project.getDependencies().add(OUTPUT_DIRS_COMPILE_CONFIGURATION_NAME, testFixturesSourceSet.getOutput());
            project.getDependencies().add(TEST_FIXTURES_USAGE_COMPILE_CONFIGURATION_NAME, configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME));
        }
    }
}
