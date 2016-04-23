package cz.quantumleap.server.autoincrement;

import cz.quantumleap.server.common.ModuleDependencyManager;
import cz.quantumleap.server.common.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class IncrementsService {

    private static final String INCREMENTS_LOCATION_PATTERN = "db/inc/v*/*.sql";
    private static final Pattern INCREMENT_VERSION_PATTERN = Pattern.compile("db/inc/v([0-9]+)/.*.sql$");

    @Autowired
    ModuleDependencyManager moduleDependencyManager;

    @Autowired
    ResourceManager resourceManager;

    public Map<String, Integer> getLatestIncrementVersionForModules() {
        Map<String, Integer> latestIncrementForModules = new HashMap<>();
        loadAllIncrements().forEach(increment ->
                latestIncrementForModules.compute(increment.getModuleName(), (moduleName, version) -> {
                    Matcher matcher = INCREMENT_VERSION_PATTERN.matcher(increment.getResourcePath());
                    if (matcher.find()) {
                        int incrementVersion = Integer.parseInt(matcher.group(1));
                        version = version == null ? incrementVersion : Integer.max(version, incrementVersion);
                    }
                    return version;
                })
        );

        moduleDependencyManager.getModuleNames().forEach(moduleName -> {
            if (!latestIncrementForModules.containsKey(moduleName)) {
                latestIncrementForModules.put(moduleName, 0);
            }
        });

        return latestIncrementForModules;
    }

    public List<IncrementScript> loadAllIncrements() {
        return resourceManager.findOnClasspath(INCREMENTS_LOCATION_PATTERN).stream()
                .map(resourceWithModule -> {
                    Matcher matcher = INCREMENT_VERSION_PATTERN.matcher(resourceWithModule.getResourcePath());
                    if (matcher.find()) {
                        int incrementVersion = Integer.parseInt(matcher.group(1));
                        return new IncrementScript(resourceWithModule, incrementVersion);
                    } else {
                        throw new IllegalStateException("Unknown increment file path " + resourceWithModule.getResourcePath() + "!");
                    }
                })
                .collect(Collectors.toList());
    }

    public static class IncrementScript {
        private final ResourceManager.ResourceWithModule resourceWithModule;
        private final int incrementVersion;

        public IncrementScript(ResourceManager.ResourceWithModule resourceWithModule, int incrementVersion) {
            this.resourceWithModule = resourceWithModule;
            this.incrementVersion = incrementVersion;
        }

        public String getModuleName() {
            return this.resourceWithModule.getModuleName();
        }

        public int getIncrementVersion() {
            return incrementVersion;
        }

        public Resource getScript() {
            return resourceWithModule.getResource();
        }

        public String getResourcePath() {
            return resourceWithModule.getResourcePath();
        }
    }
}
