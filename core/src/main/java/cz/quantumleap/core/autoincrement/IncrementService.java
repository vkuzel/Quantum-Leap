package cz.quantumleap.core.autoincrement;

import cz.quantumleap.core.module.ModuleDependencyManager;
import cz.quantumleap.core.resource.ResourceManager;
import cz.quantumleap.core.resource.ResourceWithModule;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class IncrementService {

    private static final String INCREMENTS_LOCATION_PATTERN = "db/inc/v*/*.sql";
    private static final Pattern INCREMENT_VERSION_PATTERN = Pattern.compile("db/inc/v([0-9]+)/.*.sql$");

    private final ModuleDependencyManager moduleDependencyManager;
    private final ResourceManager resourceManager;

    public IncrementService(ModuleDependencyManager moduleDependencyManager, ResourceManager resourceManager) {
        this.moduleDependencyManager = moduleDependencyManager;
        this.resourceManager = resourceManager;
    }

    public Map<String, Integer> getLatestIncrementVersionForModules() {
        Map<String, Integer> latestIncrementForModules = new HashMap<>();
        findAllIncrementsInClasspath().forEach(increment ->
                latestIncrementForModules.compute(increment.getModuleName(), (moduleName, version) -> {
                    var matcher = INCREMENT_VERSION_PATTERN.matcher(increment.getResourcePath());
                    if (matcher.find()) {
                        var incrementVersion = Integer.parseInt(matcher.group(1));
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

    public List<IncrementScript> findAllIncrementsInClasspath() {
        return resourceManager.findInClasspath(INCREMENTS_LOCATION_PATTERN).stream()
                .map(resourceWithModule -> {
                    var matcher = INCREMENT_VERSION_PATTERN.matcher(resourceWithModule.getResourcePath());
                    if (matcher.find()) {
                        var incrementVersion = Integer.parseInt(matcher.group(1));
                        return new IncrementScript(resourceWithModule, incrementVersion);
                    } else {
                        throw new IllegalStateException("Unknown increment file path " + resourceWithModule.getResourcePath() + "!");
                    }
                })
                .collect(Collectors.toList());
    }

    public static class IncrementScript {
        private final ResourceWithModule resourceWithModule;
        private final int incrementVersion;

        public IncrementScript(ResourceWithModule resourceWithModule, int incrementVersion) {
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
