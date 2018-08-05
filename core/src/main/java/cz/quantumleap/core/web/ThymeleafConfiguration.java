package cz.quantumleap.core.web;

import cz.quantumleap.core.filestorage.FileStorageManager;
import cz.quantumleap.core.resource.ResourceManager;
import cz.quantumleap.core.web.template.QuantumLeapDialect;
import cz.quantumleap.core.web.template.ThemeTemplateResolver;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Configuration
@ConditionalOnWebApplication
public class ThymeleafConfiguration {

    private final ThymeleafProperties thymeleafProperties;
    private final ResourceManager resourceManager;
    private final MessageSource messageSource;
    private final FileStorageManager fileStorageManager;

    public ThymeleafConfiguration(ThymeleafProperties thymeleafProperties, ResourceManager resourceManager, MessageSource messageSource, FileStorageManager fileStorageManager) {
        this.thymeleafProperties = thymeleafProperties;
        this.resourceManager = resourceManager;
        this.messageSource = messageSource;
        this.fileStorageManager = fileStorageManager;
    }

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(new ThemeTemplateResolver(thymeleafProperties, resourceManager));
        engine.addDialect(new SpringSecurityDialect());
        engine.addDialect(new LayoutDialect());
        engine.addDialect(new Java8TimeDialect());
        engine.addDialect(new QuantumLeapDialect(fileStorageManager));
        return engine;
    }
}
