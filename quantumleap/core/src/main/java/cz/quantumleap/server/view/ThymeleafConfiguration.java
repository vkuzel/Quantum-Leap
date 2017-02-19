package cz.quantumleap.server.view;

import cz.quantumleap.core.resource.ResourceManager;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;

@Configuration
public class ThymeleafConfiguration {

    private final ThymeleafProperties thymeleafProperties;
    private final cz.quantumleap.core.resource.ResourceManager resourceManager;

    public ThymeleafConfiguration(ThymeleafProperties thymeleafProperties, ResourceManager resourceManager) {
        this.thymeleafProperties = thymeleafProperties;
        this.resourceManager = resourceManager;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(new ThemeTemplateResolver(thymeleafProperties, resourceManager));
        engine.addDialect(new SpringSecurityDialect());
// TODO What's this good for?
        engine.addDialect(new LayoutDialect());
// TODO Does Thymeleaf need any message source?
//        engine.setTemplateEngineMessageSource(databaseMessageSource);
        return engine;
    }
}
