package cz.quantumleap.server.datafilter;

import cz.quantumleap.server.datafilter.web.SliceRequestMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class DataFilterWebSupportConfiguration extends WebMvcConfigurerAdapter {

    // Comma is used to create an array in SpEL. That causes problems while
    // building order queries in Thymeleaf so semicolon is used instead.
    private static final String SORT_COLUMN_ORDER_DELIMITER = ";";

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(createSortResolver());
        argumentResolvers.add(createPageableResolver());
    }

    private SliceRequestMethodArgumentResolver createPageableResolver() {
        return new SliceRequestMethodArgumentResolver(createSortResolver());
    }

    private SortHandlerMethodArgumentResolver createSortResolver() {
        SortHandlerMethodArgumentResolver resolver = new SortHandlerMethodArgumentResolver();
        resolver.setPropertyDelimiter(SORT_COLUMN_ORDER_DELIMITER);
        return resolver;
    }
}
