package cz.quantumleap.cli;

import cz.quantumleap.cli.environment.EnvironmentBuilder;
import cz.quantumleap.core.autoincrement.IncrementDao;
import cz.quantumleap.core.autoincrement.IncrementService;
import cz.quantumleap.core.data.RecordAuditor;
import cz.quantumleap.core.module.ModuleDependencyManager;
import cz.quantumleap.core.resource.ResourceManager;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(proxyTargetClass = true)
public class CliApplication implements CommandLineRunner {

    private final EnvironmentBuilder environmentBuilder;

    public CliApplication(EnvironmentBuilder environmentBuilder) {
        this.environmentBuilder = environmentBuilder;
    }

    @Override
    public void run(String... args) {
        String firstArg = args.length > 0 ? args[0] : "";
        switch (firstArg) {
            case "rebuild":
                environmentBuilder.dropEnvironment();
            case "build":
                environmentBuilder.buildEnvironment();
                break;
            default:
                if (StringUtils.isBlank(firstArg)) {
                    String msg = "No argument has been specified!\n" +
                            "\n" +
                            "    build - for creating new environment (database).\n" +
                            "    rebuild - for removing existing environment and creating a new one.\n";
                    System.out.println(msg);
                } else {
                    throw new IllegalArgumentException("Unknown argument " + firstArg + "!");
                }
        }
    }

    /**
     * Originally I split the project into three packages: cli, server, core.
     * Cli for cli-related stuff, server for view and most of the business
     * logic and core for DAOs and things shared between cli and server. But
     * cli had only few beans in it so I decided to drop the structure and
     * build shared beans manually in this configuration class.
     */
    @Configuration
    public static class CoreApplicationContext {

        private final DSLContext dslContext;

        public CoreApplicationContext(DSLContext dslContext) {
            this.dslContext = dslContext;
        }

        @Bean
        public ModuleDependencyManager moduleDependencyManager() {
            return new ModuleDependencyManager();
        }

        @Bean
        public ResourceManager resourceManager(ModuleDependencyManager moduleDependencyManager) {
            return new ResourceManager(moduleDependencyManager);
        }

        @Bean
        public IncrementService incrementService(ModuleDependencyManager moduleDependencyManager, ResourceManager resourceManager) {
            return new IncrementService(moduleDependencyManager, resourceManager);
        }

        @Bean
        public IncrementDao incrementDao() {
            return new IncrementDao(dslContext, null, null, new RecordAuditor());
        }
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(CliApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }
}
