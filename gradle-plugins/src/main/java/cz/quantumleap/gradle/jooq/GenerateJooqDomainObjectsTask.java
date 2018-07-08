package cz.quantumleap.gradle.jooq;

import cz.quantumleap.gradle.utils.ProjectUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.Configuration;
import org.jooq.util.jaxb.Jdbc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

public class GenerateJooqDomainObjectsTask extends DefaultTask {

    private static final String GENERATED_SRC_PATH = "src/generated/java";
    private static final String GENERATED_CLASSES_PACKAGE_PREFIX = "cz.quantumleap.";

    private static final String JOOQ_GENERATOR_CONFIGURATION_PATH = "db/jooq-generator-configuration.xml";
    private static final String APPLICATION_CONFIGURATION_PATH = "config/application-default.properties";

    private static final String DATASOURCE_URL_PROPERTY_NAME = "spring.datasource.url";
    private static final String DATASOURCE_USERNAME_PROPERTY_NAME = "spring.datasource.username";
    private static final String DATASOURCE_PASSWORD_PROPERTY_NAME = "spring.datasource.password";

    @TaskAction
    public void generate() throws Exception {
        Project project = getProject();

        Configuration configuration = initJooqGeneratorConfiguration(project);

        GenerationTool.generate(configuration);
    }

    private Configuration initJooqGeneratorConfiguration(Project project) throws IOException {
        Configuration configuration = loadJooqConfiguration(project);

        configuration.getGenerator().getDatabase().setInputSchema(project.getName());

        Properties properties = loadJdbcProperties(project);
        applyJdbcProperties(properties, configuration.getJdbc());

        configuration.getGenerator().getTarget().setDirectory(getGeneratedSrcPath(project).getAbsolutePath());
        configuration.getGenerator().getTarget().setPackageName(getGeneratedClassesPackageName(project));

        return configuration;
    }

    private void applyJdbcProperties(Properties properties, Jdbc jdbc) {
        jdbc
                .withUrl(properties.getProperty(DATASOURCE_URL_PROPERTY_NAME))
                .withUser(properties.getProperty(DATASOURCE_USERNAME_PROPERTY_NAME))
                .withPassword(properties.getProperty(DATASOURCE_PASSWORD_PROPERTY_NAME));

    }

    private Configuration loadJooqConfiguration(Project project) throws IOException {
        File file = ProjectUtils.findFileInProjectResources(project.getRootProject(), JOOQ_GENERATOR_CONFIGURATION_PATH);

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return GenerationTool.load(inputStream);
        }
    }

    private Properties loadJdbcProperties(Project project) throws IOException {
        File file = ProjectUtils.findFileInProjectResources(project.getRootProject(), APPLICATION_CONFIGURATION_PATH);

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            properties.load(inputStream);
        }
        return properties;
    }

    static File getGeneratedSrcPath(Project project) {
        return new File(project.getProjectDir(), GENERATED_SRC_PATH);
    }

    private static String getGeneratedClassesPackageName(Project project) {
        return GENERATED_CLASSES_PACKAGE_PREFIX + project.getName();
    }
}
