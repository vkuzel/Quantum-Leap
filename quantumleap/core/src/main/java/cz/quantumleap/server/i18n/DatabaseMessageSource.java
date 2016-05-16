package cz.quantumleap.server.i18n;

import com.ibm.icu.text.MessageFormat;
import cz.quantumleap.server.i18n.domain.Language;
import cz.quantumleap.server.i18n.domain.Message;
import cz.quantumleap.server.i18n.repository.LanguageRepository;
import cz.quantumleap.server.i18n.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class DatabaseMessageSource implements MessageSource {

    private Map<String, Map<String, Object>> messages;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        // TODO If no message is found for specified locale return message for default local (english!) or just some other random...
        Object messageOrFormat = messages.getOrDefault(locale.getLanguage(), Collections.emptyMap()).get(code);
        if (messageOrFormat != null) {
            return formatMessage(messageOrFormat, args);
        } else if (defaultMessage != null) {
            messageOrFormat = resolveMessagePattern(defaultMessage, locale);
            return formatMessage(messageOrFormat, args);
        }
        return null;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return getMessage(code, args, null, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        for (String code : resolvable.getCodes()) {
            String message = getMessage(code, resolvable.getArguments(), locale);
            if (message != null) {
                return message;
            }
        }
        if (resolvable.getDefaultMessage() != null) {
            Object messageOrFormat = resolveMessagePattern(resolvable.getDefaultMessage(), locale);
            return formatMessage(messageOrFormat, resolvable.getArguments());
        }
        return null;
    }

    private Object resolveMessagePattern(String messagePattern, Locale locale) {
        if (messagePattern.indexOf('{') > -1 && messagePattern.indexOf('}') > -1) {
            return new MessageFormat(messagePattern, locale);
        }
        return messagePattern;
    }

    private String formatMessage(Object messageOrFormat, Object[] args) {
        if (messageOrFormat instanceof MessageFormat) {
            MessageFormat messageFormat = (MessageFormat) messageOrFormat;
            StringBuffer formattedMessage = new StringBuffer();
            messageFormat.format(args, formattedMessage, null);
            return formattedMessage.toString();
        } else {
            return (String) messageOrFormat;
        }
    }

    // TODO Reload messages on translation or language modification! Can be put into repository into some pre-modification hook.
    private void loadMessages() {
        List<Language> languages = languageRepository.findAll();
        this.messages = new HashMap<>(languages.size());
        for (Language language : languages) {
            Locale locale = new Locale(language.getIsoCode());
            List<Message> messages = messageRepository.findByLanguage(language.getIsoCode());
            HashMap<String, Object> messageOrFormats = new HashMap<>(messages.size());

            messages.forEach(message -> messageOrFormats.put(message.getCode(), resolveMessagePattern(message.getMessage(), locale)));
            this.messages.put(language.getIsoCode(), messageOrFormats);
        }
    }

    @PostConstruct
    private void init() {
        loadMessages();
    }
}
