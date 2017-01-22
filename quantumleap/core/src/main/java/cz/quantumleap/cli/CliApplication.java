package cz.quantumleap.cli;

import cz.quantumleap.cli.environment.EnvironmentBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@SpringBootApplication
@ComponentScan(basePackages = {"cz.quantumleap.cli", "cz.quantumleap.core"})
@EnableTransactionManagement
public class CliApplication implements CommandLineRunner {

    private final EnvironmentBuilder environmentBuilder;

    public CliApplication(EnvironmentBuilder environmentBuilder) {
        this.environmentBuilder = environmentBuilder;
    }

    @Override
    public void run(String... args) throws Exception {
        String firstArg = args.length > 0 ? args[0] : "";
        switch (firstArg) {
            case "rebuild":
                environmentBuilder.dropEnvironment();
            case "build":
                environmentBuilder.buildEnvironment();
                break;
            default:
                throw new IllegalArgumentException("Unknown command " + firstArg + "!");
        }
    }

    public static void main(String[] args) throws IOException {
        SpringApplication application = new SpringApplication(CliApplication.class);
        application.setWebEnvironment(false);
        application.run(args);
    }
}
