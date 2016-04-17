package cz.quantumleap.server;

import cz.quantumleap.server.autoincrement.IncrementsRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(QuantumLeapApplication.class)
public class QuantumLeapApplicationTests {

	@Autowired
	private IncrementsRunner incrementsRunner;

	@Test
	public void contextLoads() {

		Assert.assertTrue(incrementsRunner != null);

	}
}
