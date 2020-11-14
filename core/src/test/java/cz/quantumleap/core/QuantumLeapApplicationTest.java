package cz.quantumleap.core;

import cz.quantumleap.core.autoincrement.IncrementRunner;
import cz.quantumleap.server.test.ServerSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServerSpringBootTest
public class QuantumLeapApplicationTest {

	@Autowired
	private IncrementRunner incrementRunner;

	@Test
	public void contextLoads() {

		Assertions.assertNotNull(incrementRunner);
	}
}
