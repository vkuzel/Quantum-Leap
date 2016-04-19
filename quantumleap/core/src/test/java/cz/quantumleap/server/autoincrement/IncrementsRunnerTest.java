package cz.quantumleap.server.autoincrement;

import com.google.common.collect.ImmutableMap;
import cz.quantumleap.server.QuantumLeapApplication;
import cz.quantumleap.server.config.TestEnvironmentContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptStatementFailedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@SpringApplicationConfiguration({QuantumLeapApplication.class, TestEnvironmentContext.class})
public class IncrementsRunnerTest {

    private static final Logger log = LoggerFactory.getLogger(IncrementsRunnerTest.class);

    @Autowired
    private IncrementsService incrementsService;

    @Autowired
    private IncrementsRunner incrementsRunner;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void runIncrementTest() {
        String locationPattern = "test_db/inc/v*/*.sql";
        Pattern versionPattern = Pattern.compile("test_db/inc/v([0-9]+)/.*.sql$");

        JdbcTestUtils.deleteFromTables(jdbcTemplate, "increment");

        Map<String, Integer> lastIncrements = ImmutableMap.of("core", 0);
        List<IncrementsService.IncrementResource> incrementResources = incrementsService.findAllIncrements(locationPattern, versionPattern);

        try {
            incrementsRunner.runIncrements(lastIncrements, incrementResources);
        } catch (ScriptStatementFailedException e) {
            log.debug(e.getMessage());
        }

        // TODO Assert on function created...
        Assert.assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "increment"));
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "inc_test_entity"));
    }
}
