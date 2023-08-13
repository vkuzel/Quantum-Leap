package cz.quantumleap.gradle.jooq;

import cz.quantumleap.gradle.project.SpringBootProject;
import cz.quantumleap.gradle.utils.ProjectUtils;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;

public class JooqDomainObjectsGeneratorConfigurer {

    private static final String GENERATE_JOOQ_DOMAIN_OBJECTS_TASK_NAME = "generateJooqDomainObjects";

    public void configure(SpringBootProject springBootProject) {
        var projects = springBootProject
                .getProject()
                .getRootProject()
                .getAllprojects();

        for (var project : projects) {
            this.addGeneratedSrcDirToMainSourceSet(project);

            var generate = project.getTasks()
                    .create(GENERATE_JOOQ_DOMAIN_OBJECTS_TASK_NAME, GenerateJooqDomainObjectsTask.class);
            describeTask(generate);
        }
    }

    private void addGeneratedSrcDirToMainSourceSet(Project project) {
        var mainSourceSet = ProjectUtils.getSourceSets(project).getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        mainSourceSet.getJava().srcDir(GenerateJooqDomainObjectsTask.getGeneratedSrcPath(project));
    }

    private void describeTask(GenerateJooqDomainObjectsTask task) {
        var description = "Generates jOOQ domain objects into src/generated/java directories." +
                " Generator works only with PostgreSQL." +
                " It reads jOOQ configuration from `db/jooq-generator-configuration.xml' and connection properties from `config/application-default.properties'. " +
                " Following connection properties are recognized: " +
                "     spring.datasource.url" +
                "     spring.datasource.username" +
                "     spring.datasource.password" +
                " Domain objects for every module in multi-module project must be placed in a schema of a same name as module." +
                " Generated classes will be persisted in `src/generated/java'.";
        task.setDescription(description);
        task.setGroup("build");
    }
}
