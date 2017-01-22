package cz.quantumleap.server;

import cz.quantumleap.core.autoincrement.IncrementRunner;
import cz.quantumleap.server.test.ServerSpringBootTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ServerSpringBootTest
public class QuantumLeapApplicationTest {

	@Autowired
	private IncrementRunner incrementRunner;

	@Test
	public void contextLoads() {

		Assert.assertTrue(incrementRunner != null);
	}
}
