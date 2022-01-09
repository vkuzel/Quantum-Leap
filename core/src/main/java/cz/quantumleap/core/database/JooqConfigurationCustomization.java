package cz.quantumleap.core.database;

import org.jetbrains.annotations.Nullable;
import org.jooq.Converter;
import org.jooq.ConverterProvider;
import org.jooq.impl.DefaultConverterProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class JooqConfigurationCustomization {

    @Bean
    public DefaultConfigurationCustomizer jooqConverterProviderConfigurationCustomizer(
            ObjectProvider<ConverterProvider> converterProviders
    ) {
        List<ConverterProvider> sortedProviders = converterProviders.orderedStream().toList();
        ConverterProvider delegatingProvider = new DelegatingConverterProvider(sortedProviders);
        return (configuration) -> configuration.set(delegatingProvider);
    }

    private static class DelegatingConverterProvider implements ConverterProvider {

        private final List<ConverterProvider> converterProviders;

        public DelegatingConverterProvider(List<ConverterProvider> converterProviders) {
            List<ConverterProvider> providers = new ArrayList<>(converterProviders.size() + 1);
            providers.addAll(converterProviders);
            providers.add(new DefaultConverterProvider());
            this.converterProviders = providers;
        }

        @Override
        public @Nullable <T, U> Converter<T, U> provide(Class<T> tType, Class<U> uType) {
            for (ConverterProvider converterProvider : converterProviders) {
                Converter<T, U> converter = converterProvider.provide(tType, uType);
                if (converter != null) {
                    return converter;
                }
            }
            return null;
        }
    }
}
