package cz.quantumleap.core.view;

import com.google.common.base.Charsets;
import cz.quantumleap.core.resource.ResourceManager;
import cz.quantumleap.core.resource.ResourceWithModule;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

import static java.util.Collections.enumeration;

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

    private static final InputStream EMPTY_LINE_INPUT_STREAM = new ByteArrayInputStream("\n".getBytes());

    // ResourceBundleMessageSource does not support multiple messages
    // properties files with a same name on the classpath,
    // e.g. message.properties
    //
    // Quantum Leap does support multiple modules => there are multiple
    // messages files with the same name on the classpath. This custom
    // input stream will bypass that limitation.
    private InputStream getResourcesInputStream(String resourceName) {
        List<InputStream> inputStreams = new ArrayList<>();
        for (ResourceWithModule resource : resourceManager.findInClasspath(resourceName)) {
            inputStreams.add(resource.getInputStream());
            // Handle messages file with missing newline at the end of the file
            inputStreams.add(createEmptyLineInputStream());
        }
        return new SequenceInputStream(enumeration(inputStreams));
    }

    private InputStream createEmptyLineInputStream() {
        return new ByteArrayInputStream("\n".getBytes());
    }
}
