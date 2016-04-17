package cz.quantumleap.server.autoincrement;

import cz.quantumleap.server.QuantumLeapApplication;
import cz.quantumleap.server.config.TestEnvironmentContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({QuantumLeapApplication.class, TestEnvironmentContext.class})
public class IncrementsRunnerTest {

    @Autowired
    private IncrementsRunner incrementsRunner;

    @Test
    public void runIncrementTest() {
        Assert.assertTrue(incrementsRunner != null);
    }
}
