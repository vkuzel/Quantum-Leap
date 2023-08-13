package cz.quantumleap.core.view.template;

import cz.quantumleap.core.resource.ResourceManager;
import org.apache.commons.lang3.Validate;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.util.Map;

public class ThemeTemplateResolver extends AbstractConfigurableTemplateResolver {

    private static final String RESOLVER_NAME = "Quantum Leap template resolver";

    private final ResourceManager resourceManager;

    public ThemeTemplateResolver(ThymeleafProperties thymeleafProperties, ResourceManager resourceManager) {
        super();
        setOrder(thymeleafProperties.getTemplateResolverOrder());
        setName(RESOLVER_NAME);

        setTemplateMode(thymeleafProperties.getMode());
        validatePrefix(thymeleafProperties.getPrefix());
        setPrefix(thymeleafProperties.getPrefix());
        setSuffix(thymeleafProperties.getSuffix());
        setCharacterEncoding(thymeleafProperties.getEncoding().name());
        setCacheable(thymeleafProperties.isCache());

        this.resourceManager = resourceManager;
    }

    private void validatePrefix(String prefix) {
        Validate.notNull(prefix);
        if (prefix.startsWith("classpath")) {
            throw new IllegalArgumentException("Do not use classpath prefix for template paths. The prefix will be added automatically.");
        }
    }

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        var optional = resourceManager.findMostSpecificInClasspathOrWorkingDir(resourceName);
        return optional
                .map(resource -> new ThemeTemplateResource(resource, getCharacterEncoding()))
                .orElseThrow(() -> new IllegalStateException("No template found for " + resourceName));
    }

}
