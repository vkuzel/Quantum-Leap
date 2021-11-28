package cz.quantumleap.core.view;

import com.google.common.base.Charsets;
import cz.quantumleap.core.resource.ResourceManager;
import cz.quantumleap.core.resource.ResourceWithModule;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.*;

@Component(AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME)
public class MultiResourceMessageSource extends ResourceBundleMessageSource {

    private final ResourceManager resourceManager;

    public MultiResourceMessageSource(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
        this.setBasename("messages");
        this.setDefaultEncoding(Charsets.UTF_8.name());
        this.setFallbackToSystemLocale(true);
    }

    @Override
    protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
        return ResourceBundle.getBundle(basename, locale, getBundleClassLoader(), new MessageSourceControl());
    }

    private class MessageSourceControl extends ResourceBundle.Control {

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {

            // Special handling of default encoding
            if (format.equals("java.properties")) {
                String bundleName = toBundleName(baseName, locale);
                final String resourceName = toResourceName(bundleName, "properties");
                try (InputStream stream = getResourcesInputStream(resourceName)) {
                    String encoding = getDefaultEncoding();
                    return loadBundle(new InputStreamReader(stream, encoding));
                }
            } else {
                // Delegate handling of "java.class" format to standard Control
                return super.newBundle(baseName, locale, format, loader, reload);
            }
        }
    }

    // Quantum Leap supports multiple modules this means that messages can be
    // distributed between those modules. Spring's ResourceBundleMessageSource
    // can't handle multiple messages files of the same name on classpath.
    private InputStream getResourcesInputStream(String resourceName) {
        List<ResourceWithModule> resources = resourceManager.findInClasspath(resourceName);
        Iterator<ResourceWithModule> iterator = resources.iterator();
        return new SequenceInputStream(new Enumeration<>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public InputStream nextElement() {
                return iterator.next().getInputStream();
            }
        });
    }
}
