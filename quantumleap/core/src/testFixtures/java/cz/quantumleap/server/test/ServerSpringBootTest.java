package cz.quantumleap.server.test;


import cz.quantumleap.QuantumLeapApplication;
import cz.quantumleap.core.test.CoreTestContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootTest(classes = {QuantumLeapApplication.class, CoreTestContext.class})
@ActiveProfiles("test")
public @interface ServerSpringBootTest {
}
