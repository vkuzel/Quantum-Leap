package cz.quantumleap.core.autoincrement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import cz.quantumleap.core.database.TransactionExecutor;
import cz.quantumleap.core.module.ModuleDependencies;
import cz.quantumleap.core.resource.ResourceWithModule;
import cz.quantumleap.core.test.CoreSpringBootTest;
import cz.quantumleap.core.test.common.CoreTestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.BadSqlGrammarException;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@CoreSpringBootTest
public class IncrementRunnerTest {

    private static final Logger log = LoggerFactory.getLogger(IncrementRunnerTest.class);

    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Autowired
    private CoreTestSupport testSupport;
    @Autowired
    private Environment environment;
    @Autowired
    private IncrementService incrementService;
    @Autowired
    private TransactionExecutor transactionExecutor;
    @Autowired
    private IncrementDao incrementDao;

    @Test
    public void newIncrementsAreExecuted() {
        List<IncrementService.IncrementScript> incrementScripts = ImmutableList.of(
                createIncrementScript("classpath:/test_db/inc/v01/01_testEntity.sql", 1),
                createIncrementScript("classpath:/test_db/inc/v02/01_testFunction.sql", 2),
                createIncrementScript("classpath:/test_db/inc/v03/01_validScript.sql", 3),
                createIncrementScript("classpath:/test_db/inc/v03/02_invalidScript.sql", 3)
        );

        testSupport.deleteFromTable("core.increment");
        Map<String, Integer> lastIncrements = ImmutableMap.of("core", 0);
        IncrementRunner incrementRunner = new IncrementRunner(environment, incrementService, transactionExecutor, incrementDao);

        try {
            incrementRunner.runIncrements(lastIncrements, incrementScripts);
        } catch (BadSqlGrammarException e) {
            log.debug(e.getMessage());
        }

        Assertions.assertEquals(2, testSupport.countRowsInTable("core.increment"));
        Assertions.assertEquals(0, testSupport.countRowsInTable("core.test_entity"));
        Assertions.assertEquals(0, testSupport.countRowsInTable("core.related_test_entity"));
        Assertions.assertEquals(2, testSupport.fetchFirst("SELECT core.increment(1)", Integer.class));
    }

    private IncrementService.IncrementScript createIncrementScript(String locationPattern, int incrementVersion) {
        ModuleDependencies module = mock(ModuleDependencies.class);
        when(module.getModuleName()).thenReturn("core");
        Resource resource = resourceResolver.getResource(locationPattern);
        return new IncrementService.IncrementScript(
                new ResourceWithModule(module, resource),
                incrementVersion
        );
    }
}
