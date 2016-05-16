package cz.quantumleap.server.i18n;

import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Deprecated
public class TranslationService { // TODO Remove this service we don't need this!

    // ResourceBundleMessageSource
    // ReloadableResourceBundleMessageSource
    // This: http://blog.javaforge.net/post/32188367580/database-driven-message-source-in-spring
    // http://codedevstuff.blogspot.cz/2015/05/spring-boot-internationalization-with.html

    private String defaultLanguage = "en-US";

    public String translate(String text) {
        return translate(defaultLanguage, text);
    }

    public String translate(String language, String text) {
        return Objects.equals(language, defaultLanguage) ? text : "/prelozeno";
    }

    public List<String> getLanguages() { // TODO There have to be some existing structures for this!
        return ImmutableList.of(defaultLanguage, "cs-CZ");
    }
}
