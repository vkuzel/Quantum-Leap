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
        return findAllIncrements(INCREMENTS_LOCATION_PATTERN, INCREMENT_VERSION_PATTERN);
    }

    List<IncrementScript> findAllIncrements(String locationPattern, Pattern versionPattern) {
        return resourceManager.findOnClasspath(locationPattern).stream()
                .map(moduleResource -> {
                    Matcher matcher = versionPattern.matcher(moduleResource.getResourcePath());
                    if (matcher.find()) {
                        int incrementVersion = Integer.parseInt(matcher.group(1));
                        return new IncrementScript(moduleResource, incrementVersion);
                    } else {
                        throw new IllegalStateException("Unknown increment file path " + moduleResource.getResourcePath() + "!");
                    }
                })
                .collect(Collectors.toList());
    }

    public static class IncrementScript {
        private final ResourceManager.ModuleResource moduleResource;
        private final int incrementVersion;

        public IncrementScript(ResourceManager.ModuleResource moduleResource, int incrementVersion) {
            this.moduleResource = moduleResource;
            this.incrementVersion = incrementVersion;
        }

        public String getModuleName() {
            return this.moduleResource.getModuleName();
        }

        public int getIncrementVersion() {
            return incrementVersion;
        }

        public Resource getScript() {
            return moduleResource.getResource();
        }

        public String getResourcePath() {
            return moduleResource.getResourcePath();
        }

        public String getResourceFileName() {
            return moduleResource.getResourceFileName();
        }
    }
}
