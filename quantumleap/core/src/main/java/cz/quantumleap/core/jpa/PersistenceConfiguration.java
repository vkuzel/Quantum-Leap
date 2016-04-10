package cz.quantumleap.core.jpa;

import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "cz.quantumleap.*.repository")
@EntityScan(basePackages = "cz.quantumleap.*.domain")
public class PersistenceConfiguration {
}
