package cz.quantumleap.cli;

import cz.quantumleap.cli.environment.BuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@ComponentScan(basePackages = {"cz.quantumleap.cli", "cz.quantumleap.common"})
public class CliApplication implements CommandLineRunner {

    @Autowired
    private BuilderService builderService;

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
                builderService.dropEnvironment();
            case "build":
                builderService.buildEnvironment();
                break;
            default:
                throw new IllegalArgumentException("Unknown command " + firstArg + "!");
        }
    }
}
