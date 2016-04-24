package cz.quantumleap.cli.config;

import cz.quantumleap.server.autoincrement.IncrementsService;
import cz.quantumleap.server.common.ModuleDependencyManager;
import cz.quantumleap.server.common.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class CliConfiguration {

    @Autowired
    private Environment environment;

    @Bean
    public ModuleDependencyManager moduleDependencyManager() {
        return new ModuleDependencyManager();
    }

    @Bean
    public ResourceManager resourceManager() {
        return new ResourceManager();
    }

    @Bean
    public IncrementsService incrementsManager() {
        return new IncrementsService();
    }
}
