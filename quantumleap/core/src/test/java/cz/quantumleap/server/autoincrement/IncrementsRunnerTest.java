package cz.quantumleap.server.autoincrement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import cz.quantumleap.server.QuantumLeapApplication;
import cz.quantumleap.server.common.ModuleDependencyManager;
import cz.quantumleap.server.common.ResourceManager;
import cz.quantumleap.server.config.TestEnvironmentContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration({QuantumLeapApplication.class, TestEnvironmentContext.class})
public class IncrementsRunnerTest {

    private static final Logger log = LoggerFactory.getLogger(IncrementsRunnerTest.class);

    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Autowired
    private IncrementsRunner incrementsRunner;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void runIncrementTest() {
        List<IncrementsService.IncrementScript> incrementScripts = ImmutableList.of(
                createIncrementScript("classpath:/test_db/inc/v01/01_testEntity.sql", 1),
                createIncrementScript("classpath:/test_db/inc/v02/01_testFunction.sql", 2),
                createIncrementScript("classpath:/test_db/inc/v03/01_validScript.sql", 3),
                createIncrementScript("classpath:/test_db/inc/v03/02_invalidScript.sql", 3)
        );

        JdbcTestUtils.deleteFromTables(jdbcTemplate, "increment");
        Map<String, Integer> lastIncrements = ImmutableMap.of("core", 0);
        try {
            incrementsRunner.runIncrements(lastIncrements, incrementScripts);
        } catch (BadSqlGrammarException e) {
            log.debug(e.getMessage());
        }

        Assert.assertEquals(2, JdbcTestUtils.countRowsInTable(jdbcTemplate, "increment"));
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "test_entity"));
        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, "related_test_entity"));
        Assert.assertEquals(2, (int) jdbcTemplate.queryForObject("SELECT increment(1)", Integer.class));
    }

    private IncrementsService.IncrementScript createIncrementScript(String locationPattern, int incrementVersion) {
        ModuleDependencyManager.Dependencies module = Mockito.mock(ModuleDependencyManager.Dependencies.class);
        Mockito.when(module.getModuleName()).thenReturn("core");
        return new IncrementsService.IncrementScript(
                new ResourceManager.ResourceWithModule(
                        module,
                        resourceResolver.getResource(locationPattern)
                ),
                incrementVersion
        );
    }
}
