package cz.quantumleap.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

@SpringBootApplication
// @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "cz.quantumleap.server")
public class QuantumLeapApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(QuantumLeapApplication.class, args);
    }
}
