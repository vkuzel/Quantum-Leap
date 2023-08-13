package cz.quantumleap.core.view;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import cz.quantumleap.core.filestorage.FileStorageManager;
import cz.quantumleap.core.resource.ResourceManager;
import cz.quantumleap.core.view.template.QuantumLeapDialect;
import cz.quantumleap.core.view.template.ThemeTemplateResolver;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.dialect.SpringStandardDialect;
import org.thymeleaf.standard.serializer.IStandardJavaScriptSerializer;

import java.io.IOException;
import java.io.Writer;

@Configuration
@ConditionalOnWebApplication
public class ThymeleafConfiguration {

    @Bean
    public SpringTemplateEngine thymeleafTemplateEngine(
            ThymeleafProperties thymeleafProperties,
            ResourceManager resourceManager,
            FileStorageManager fileStorageManager,
            ObjectMapper objectMapper
    ) {
        var mapper = objectMapper.copy();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        var serializationConfig = mapper.getSerializationConfig()
                .with(JsonWriteFeature.ESCAPE_NON_ASCII);
        mapper.setConfig(serializationConfig);
        mapper.getFactory().setCharacterEscapes(new JacksonThymeleafCharacterEscapes());

        var engine = new SpringTemplateEngine();
        var dialect = new SpringStandardDialect();
        dialect.setJavaScriptSerializer(new JavaScriptSerializer(mapper));
        engine.setDialect(dialect);

        engine.setTemplateResolver(new ThemeTemplateResolver(thymeleafProperties, resourceManager));
        engine.addDialect(new SpringSecurityDialect());
        engine.addDialect(new LayoutDialect());
        engine.addDialect(new QuantumLeapDialect(fileStorageManager));
        return engine;
    }

    /**
     * Copied from {@link org.thymeleaf.standard.serializer.StandardJavaScriptSerializer}
     */
    private static class JacksonThymeleafCharacterEscapes extends CharacterEscapes {

        private static final int[] CHARACTER_ESCAPES;
        private static final SerializableString SLASH_ESCAPE = new SerializedString("\\/");
        private static final SerializableString AMPERSAND_ESCAPE = new SerializedString("\\u0026");

        static {
            CHARACTER_ESCAPES = CharacterEscapes.standardAsciiEscapesForJSON();
            CHARACTER_ESCAPES['/'] = CharacterEscapes.ESCAPE_CUSTOM;
            CHARACTER_ESCAPES['&'] = CharacterEscapes.ESCAPE_CUSTOM;
        }

        @Override
        public int[] getEscapeCodesForAscii() {
            return CHARACTER_ESCAPES;
        }

        @Override
        public SerializableString getEscapeSequence(final int ch) {
            if (ch == '/') {
                return SLASH_ESCAPE;
            } else if (ch == '&') {
                return AMPERSAND_ESCAPE;
            } else {
                return null;
            }
        }
    }

    /**
     * Copied from {@link org.thymeleaf.standard.serializer.StandardJavaScriptSerializer}
     */
    private static class JavaScriptSerializer implements IStandardJavaScriptSerializer {

        private final ObjectMapper mapper;

        public JavaScriptSerializer(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public void serializeValue(Object object, Writer writer) {
            try {
                mapper.writeValue(writer, object);
            } catch (IOException e) {
                var msg = "An exception was raised while trying to serialize object to JavaScript using Jackson";
                throw new TemplateProcessingException(msg, e);
            }
        }
    }
}
