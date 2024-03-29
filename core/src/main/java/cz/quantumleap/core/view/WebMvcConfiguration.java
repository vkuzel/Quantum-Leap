package cz.quantumleap.core.view;

import cz.quantumleap.core.filestorage.FileStorageManager;
import cz.quantumleap.core.view.controllerargument.FetchParamsControllerArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.util.List;

@Configuration
@ConditionalOnWebApplication
public class WebMvcConfiguration implements WebMvcConfigurer {

    // Comma is used to create an array in SpEL. That causes problems while
    // building order queries in Thymeleaf, so semicolon is used instead.
    private static final String SORT_COLUMN_ORDER_DELIMITER = ";";

    private static final String STORAGE_PATH_PATTERN = FileStorageManager.STORAGE_URL_PREFIX + "**";

    private final String fileStorageDirectory;
    private final MessageSource messageSource;

    public WebMvcConfiguration(@Value("${quantumleap.file.storage.dir}") String fileStorageDirectory, MessageSource messageSource) {
        this.fileStorageDirectory = fileStorageDirectory;
        this.messageSource = messageSource;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(createSortResolver());
        argumentResolvers.add(createPageableResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(STORAGE_PATH_PATTERN)
                .addResourceLocations(Paths.get(fileStorageDirectory).toUri().toString())
                .resourceChain(false);
    }

    private FetchParamsControllerArgumentResolver createPageableResolver() {
        return new FetchParamsControllerArgumentResolver(createSortResolver());
    }

    private SortHandlerMethodArgumentResolver createSortResolver() {
        var resolver = new SortHandlerMethodArgumentResolver();
        resolver.setPropertyDelimiter(SORT_COLUMN_ORDER_DELIMITER);
        return resolver;
    }

    // To be able to use validation messages in a form of "{message.path}" from
    // messages*.properties files. Standard Validator gets the messages from
    // ValidationMessages*.properties files.
    @Override
    public Validator getValidator() {
        var localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.setValidationMessageSource(messageSource);
        return localValidatorFactoryBean;
    }
}
