package cz.quantumleap.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import java.io.IOException;

@SpringBootApplication
@ComponentScan(
        basePackages = {"cz.quantumleap.core", "cz.quantumleap.server"},
        excludeFilters = {
                @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
                @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class)
        }
)
public class QuantumLeapApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(QuantumLeapApplication.class, args);
    }
}
