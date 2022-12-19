package cz.quantumleap.gradle.jooq;

import cz.quantumleap.gradle.utils.ProjectUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.SchemaMappingType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static org.gradle.api.tasks.SourceSet.MAIN_SOURCE_SET_NAME;

public class GenerateJooqDomainObjectsTask extends DefaultTask {

    private static final Pattern SCRIPT_FILE_NAME_PATTERN = Pattern.compile(".*_.*\\.sql$");
    private static final Pattern CREATE_SCHEMA_QUERY_PATTERN = Pattern.compile("create +schema +([^ ;]+)", CASE_INSENSITIVE);

    private static final String GENERATED_SRC_PATH = "src/generated/java";
    private static final String GENERATED_CLASSES_PACKAGE_PREFIX = "cz.quantumleap.";

    private static final String SCRIPTS_DIR = "db/scripts";
    private static final String JOOQ_GENERATOR_CONFIGURATION_PATH = "db/jooq-generator-configuration.xml";
    private static final String APPLICATION_CONFIGURATION_PATH = "config/application-default.properties";

    private static final String DATASOURCE_URL_PROPERTY_NAME = "spring.datasource.url";
    private static final String DATASOURCE_USERNAME_PROPERTY_NAME = "spring.datasource.username";
    private static final String DATASOURCE_PASSWORD_PROPERTY_NAME = "spring.datasource.password";

    @TaskAction
    public void generate() throws Exception {
        Project project = getProject();
        List<SchemaMappingType> schemata = findSchemata(project);
        if (schemata.isEmpty()) {
            return;
        }

        Configuration configuration = initJooqGeneratorConfiguration(project, schemata);
        GenerationTool.generate(configuration);
    }

    private Configuration initJooqGeneratorConfiguration(Project project, List<SchemaMappingType> schemata) throws IOException {
        Configuration configuration = loadJooqConfiguration(project);

        configuration.getGenerator().getDatabase().withSchemata(schemata);

        Properties properties = loadJdbcProperties(project);
        applyJdbcProperties(properties, configuration.getJdbc());

        configuration.getGenerator().getTarget().setDirectory(getGeneratedSrcPath(project).getAbsolutePath());
        configuration.getGenerator().getTarget().setPackageName(resolveGeneratedClassesPackageName(project));

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

    private List<SchemaMappingType> findSchemata(Project project) {
        return findResourcesPaths(project)
                .map(p -> p.resolve(SCRIPTS_DIR))
                .filter(Files::exists)
                .flatMap(this::walkDirectory)
                .filter(this::isScriptFile)
                .map(this::fileToString)
                .flatMap(this::parseSchemaNames)
                .map(this::createSchemaMappingType)
                .collect(Collectors.toList());
    }

    private Stream<Path> findResourcesPaths(Project project) {
        SourceSet mainSourceSet = ProjectUtils.getSourceSets(project).getByName(MAIN_SOURCE_SET_NAME);
        Stream.Builder<Path> builder = Stream.builder();
        for (File resource : mainSourceSet.getResources().getSrcDirs()) {
            builder.accept(resource.toPath());
        }
        return builder.build();
    }

    private Stream<Path> walkDirectory(Path path) {
        if (!path.toFile().isDirectory()) {
            throw new IllegalArgumentException("Given path " + path + " is not directory!");
        }

        try {
            return Files.walk(path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean isScriptFile(Path path) {
        Matcher matcher = SCRIPT_FILE_NAME_PATTERN.matcher(path.toString());
        return matcher.matches();
    }

    private String fileToString(Path filePath) {
        if (!filePath.toFile().isFile()) {
            throw new IllegalArgumentException("Given path " + filePath + " is not file!");
        }

        try {
            byte[] bytes = Files.readAllBytes(filePath);
            return new String(bytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Stream<String> parseSchemaNames(String content) {
        Matcher matcher = CREATE_SCHEMA_QUERY_PATTERN.matcher(content);
        Stream.Builder<String> builder = Stream.builder();
        while (matcher.find()) {
            builder.accept(matcher.group(1));
        }
        return builder.build();
    }

    private SchemaMappingType createSchemaMappingType(String schemaName) {
        SchemaMappingType schemaMappingType =  new SchemaMappingType();
        schemaMappingType.setInputSchema(schemaName);
        return schemaMappingType;
    }

    private String resolveGeneratedClassesPackageName(Project project) {
        return GENERATED_CLASSES_PACKAGE_PREFIX + project.getName();
    }
}
