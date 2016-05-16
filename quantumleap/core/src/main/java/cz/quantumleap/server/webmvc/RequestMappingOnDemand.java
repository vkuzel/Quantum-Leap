package cz.quantumleap.server.webmvc;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RequestMappingOnDemand {

    Class<? extends MappingGeneratorManager> value();
}
