package cz.quantumleap.server.autoincrement;

import cz.quantumleap.server.autoincrement.domain.Increment;
import cz.quantumleap.server.autoincrement.repository.IncrementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class IncrementsRunner {

    @Autowired
    IncrementsManager incrementsManager;

    @Autowired
    IncrementRepository incrementRepository;

    @PostConstruct
    public void runAutoIncrements() {
        Map<String, Integer> lastIncrements = incrementRepository.loadLastIncrementVersionForProjects();

        incrementsManager.loadAllIncrements().forEach(incrementResource -> {
            int lastIncrementVersion = lastIncrements.getOrDefault(incrementResource.getProjectName(), 0);
            if (incrementResource.getIncrementVersion() > lastIncrementVersion) {
                Increment increment = new Increment();
                increment.setModule(incrementResource.getProjectName());
                increment.setVersion(incrementResource.getIncrementVersion());
                increment.setFileName(incrementResource.getResourceFileName());
                increment.setCreatedAt(LocalDateTime.now());
                incrementRepository.save(increment);
            }
        });
    }
}
