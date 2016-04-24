package cz.quantumleap.server.config;

import cz.quantumleap.server.common.ResourceManager;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.resourceresolver.SpringResourceResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.util.Validate;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class ThymeleafConfiguration {

    @Autowired
    private ResourceManager resourceManager;

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(new ThemeTemplateResolver(resourceManager));
        engine.addDialect(new LayoutDialect());
        return engine;
    }

    @Bean
    public SpringResourceResourceResolver thymeleafResourceResolver() {
        return new SpringResourceResourceResolver();
    }

    public static class ThemeTemplateResolver extends TemplateResolver {

        private static final Logger log = LoggerFactory.getLogger(ThemeTemplateResolver.class);

        private static final ThymeleafProperties PROPERTIES = new ThymeleafProperties();
        private static final String TEMPLATES_DIR = "/templates/";

        private ThemeTemplateResolver(ResourceManager resourceManager) {
            super();
            setSuffix(PROPERTIES.getSuffix());
            setTemplateMode(PROPERTIES.getMode());
            if (PROPERTIES.getEncoding() != null) {
                setCharacterEncoding(PROPERTIES.getEncoding().name());
            }
            setCacheable(PROPERTIES.isCache()); // TODO Verify this!
            setResourceResolver(new IResourceResolver() {
                @Override
                public String getName() {
                    return "QUANTUMLEAP-RESOURCE-RESOLVER";
                }

                @Override
                public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameters, String resourceName) {
                    Resource template = resourceManager.findMostSpecificInClasspathOrWorkingDir(resourceName);
                    if (template != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Resource {} has been resolved for template {}", template.getFilename(), templateProcessingParameters.getTemplateName());
                        }
                        try {
                            return template.getInputStream();
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    } else {
                        throw new IllegalArgumentException("No template file has been found for " + resourceName);
                    }
                }
            });
            setOrder(Integer.MAX_VALUE);
        }

        @Override
        protected String computeResourceName(TemplateProcessingParameters templateProcessingParameters) {
            // TODO Implement localization processing into the template...nooo it should be part of resource resolver...probably
            checkInitialized();

            final String templateName = templateProcessingParameters.getTemplateName();

            Validate.notNull(templateName, "Template name cannot be null");

            String unaliasedName = this.getTemplateAliases().get(templateName);
            if (unaliasedName == null) {
                unaliasedName = templateName;
            }

            return TEMPLATES_DIR + unaliasedName + unsafeGetSuffix();
        }
    }
}
