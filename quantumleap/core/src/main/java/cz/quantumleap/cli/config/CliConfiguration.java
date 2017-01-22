package cz.quantumleap.cli.config;

import cz.quantumleap.core.autoincrement.IncrementService;
import cz.quantumleap.core.module.ModuleDependencyManager;
import cz.quantumleap.core.resource.ResourceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class CliConfiguration {

    @Bean
    public ModuleDependencyManager moduleDependencyManager() {
        return new ModuleDependencyManager();
    }

    @Bean
    public ResourceManager resourceManager(ModuleDependencyManager moduleDependencyManager) {
        return new ResourceManager(moduleDependencyManager);
    }

    @Bean
    public IncrementService incrementsManager(
            ModuleDependencyManager moduleDependencyManager,
            ResourceManager resourceManager
    ) {
        return new IncrementService(moduleDependencyManager, resourceManager);
    }
}
