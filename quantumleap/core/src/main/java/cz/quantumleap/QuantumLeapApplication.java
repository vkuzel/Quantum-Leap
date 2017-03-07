package cz.quantumleap;

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
        excludeFilters = {
                @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
                @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class),
                @Filter(type = FilterType.REGEX, pattern = "cz.quantumleap.cli\\..*")
        }
)
public class QuantumLeapApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(QuantumLeapApplication.class, args);
    }
}
