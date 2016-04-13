package cz.quantumleap.core.thymeleaf;

import com.google.common.collect.ImmutableSet;
import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.resourceresolver.SpringResourceResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.util.StringUtils;
import org.thymeleaf.util.Validate;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class TemplateResolverConfig {

    private static final String DEFAULT_THEME_NAME = "default";

    @Value("${theme.name}") // TODO Default value null ... read about SPEL one more time.
    private String themeName = null;

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        Set<ThemeTemplateResolver> resolvers = new HashSet<>(3);
        // TODO Don't include non existing directories ... check out Thymeleaf for Spring Boot autoconfiguration.
        resolvers.add(ThemeTemplateResolver.createWithCurrentDir(this.thymeleafResourceResolver(), resolvers.size() + 1));
        if (!StringUtils.isEmptyOrWhitespace(themeName)) {
            resolvers.add(ThemeTemplateResolver.createWithThemeDir(this.thymeleafResourceResolver(), themeName, resolvers.size() + 1));
        }
        resolvers.add(ThemeTemplateResolver.createWithThemeDir(this.thymeleafResourceResolver(), DEFAULT_THEME_NAME, resolvers.size() + 1));
        engine.setTemplateResolvers(resolvers);
        engine.addDialect(new LayoutDialect());
        return engine;
    }

    @Bean
    public SpringResourceResourceResolver thymeleafResourceResolver() {
        return new SpringResourceResourceResolver();
    }

    public static class ThemeTemplateResolver extends TemplateResolver {

        private static final ThymeleafProperties PROPERTIES = new ThymeleafProperties();
        private static final SpringResourceResourceResolver RESOURCE_RESOLVER = new SpringResourceResourceResolver();
        public static final String THEME_DIR_PREFIX = "classpath:/themes/";
        public static final String CURRENT_DIR_PREFIX = "file:templates/";

        public ThemeTemplateResolver() {
            super();
            setSuffix(PROPERTIES.getSuffix());
            setTemplateMode(PROPERTIES.getMode());
            if (PROPERTIES.getEncoding() != null) {
                setCharacterEncoding(PROPERTIES.getEncoding().name());
            }
            setCacheable(PROPERTIES.isCache());
        }

        public static ThemeTemplateResolver createWithThemeDir(IResourceResolver resourceResolver, String themeName, int order) {
            ThemeTemplateResolver resolver = new ThemeTemplateResolver();
            resolver.setResourceResolver(resourceResolver);
            resolver.setPrefix(THEME_DIR_PREFIX + themeName + "/");
            resolver.setOrder(order);
            return resolver;
        }

        public static ThemeTemplateResolver createWithCurrentDir(IResourceResolver resourceResolver, int order) {
            ThemeTemplateResolver resolver = new ThemeTemplateResolver();
            resolver.setResourceResolver(resourceResolver);
            resolver.setPrefix(CURRENT_DIR_PREFIX);
            resolver.setOrder(order);
            resolver.setNonCacheablePatterns(ImmutableSet.of("*"));
            return resolver;
        }

        @Override
        protected String computeResourceName(TemplateProcessingParameters templateProcessingParameters) {
            // TODO Implement localization processing into this template...
            checkInitialized();
            // TODO Template resolver using ResourceManager.findFirstSpecificFromClasspathOrWorkingDir method.
            // Found templates seems to be cached so there shouldn't be any major performance flaws.

            final String templateName = templateProcessingParameters.getTemplateName();

            Validate.notNull(templateName, "Template name cannot be null");

            String unaliasedName = this.getTemplateAliases().get(templateName);
            if (unaliasedName == null) {
                unaliasedName = templateName;
            }

            final StringBuilder resourceName = new StringBuilder();
            if (!StringUtils.isEmptyOrWhitespace(this.getPrefix())) {
                resourceName.append(this.getPrefix());
            }
            resourceName.append(unaliasedName);
            if (!StringUtils.isEmptyOrWhitespace(this.getSuffix())) {
                resourceName.append(this.getSuffix());
            }
System.out.println("resource: " + resourceName.toString());
            return resourceName.toString();
        }
    }
}
