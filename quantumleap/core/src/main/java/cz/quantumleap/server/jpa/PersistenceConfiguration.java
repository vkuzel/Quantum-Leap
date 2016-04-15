package cz.quantumleap.server.jpa;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "cz.quantumleap.server.*.repository")
@EntityScan(basePackages = "cz.quantumleap.server.*.domain")
public class PersistenceConfiguration {
}
