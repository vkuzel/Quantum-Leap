package cz.quantumleap.server.autoincrement;

import cz.quantumleap.server.common.ProjectDependencyManager;
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
public class IncrementsManager {

    private static final String INCREMENTS_LOCATION_PATTERN = "db/inc/v*/*.sql";
    private static final Pattern INCREMENT_VERSION_PATTERN = Pattern.compile("db/inc/v([0-9]+)/.*.sql$");

    @Autowired
    ProjectDependencyManager projectDependencyManager;

    @Autowired
    ResourceManager resourceManager;

    public Map<String, Integer> getLatestIncrementVersionForProjects() {
        Map<String, Integer> latestIncrementForProjects = new HashMap<>();
        loadAllIncrements().forEach(increment ->
                latestIncrementForProjects.compute(increment.getProjectName(), (projectName, version) -> {
                    Matcher matcher = INCREMENT_VERSION_PATTERN.matcher(increment.getResourcePath());
                    if (matcher.find()) {
                        int incrementVersion = Integer.parseInt(matcher.group(1));
                        version = version == null ? incrementVersion : Integer.max(version, incrementVersion);
                    }
                    return version;
                })
        );

        projectDependencyManager.getProjectNames().forEach(projectName -> {
            if (!latestIncrementForProjects.containsKey(projectName)) {
                latestIncrementForProjects.put(projectName, 0);
            }
        });

        return latestIncrementForProjects;
    }

    public List<IncrementResource> loadAllIncrements() {
        return resourceManager.findOnClasspath(INCREMENTS_LOCATION_PATTERN).stream()
                .map(projectResource -> {
                    Matcher matcher = INCREMENT_VERSION_PATTERN.matcher(projectResource.getResourcePath());
                    if (matcher.find()) {
                        int incrementVersion = Integer.parseInt(matcher.group(1));
                        return new IncrementResource(projectResource, incrementVersion);
                    } else {
                        throw new IllegalStateException("Unknown increment file path " + projectResource.getResourcePath() + "!");
                    }
                })
                .collect(Collectors.toList());
    }

    public static class IncrementResource {
        private final ResourceManager.ProjectResource projectResource;
        private final int incrementVersion;

        public IncrementResource(ResourceManager.ProjectResource projectResource, int incrementVersion) {
            this.projectResource = projectResource;
            this.incrementVersion = incrementVersion;
        }

        public String getProjectName() {
            return this.projectResource.getProjectName();
        }

        public int getIncrementVersion() {
            return incrementVersion;
        }

        public Resource getResource() {
            return projectResource.getResource();
        }

        public String getResourcePath() {
            return projectResource.getResourcePath();
        }

        public String getResourceFileName() {
            return projectResource.getResourceFileName();
        }
    }
}
