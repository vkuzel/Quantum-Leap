package cz.quantumleap.cli;

import cz.quantumleap.cli.environment.EnvironmentBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
@ComponentScan(basePackages = {"cz.quantumleap.cli"})
public class CliApplication implements CommandLineRunner {

    @Autowired
    private EnvironmentBuilderService environmentBuilderService;

    public static void main(String[] args) throws IOException {
        SpringApplication application = new SpringApplication(CliApplication.class);
        application.setWebEnvironment(false);
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        String firstArg = args.length > 0 ? args[0] : "";
        switch (firstArg) {
            case "rebuild":
                environmentBuilderService.dropEnvironment();
            case "build":
                environmentBuilderService.buildEnvironment();
                break;
            default:
                throw new IllegalArgumentException("Unknown command " + firstArg + "!");
        }
    }
}
