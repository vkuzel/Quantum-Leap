package cz.quantumleap.server.common;

import com.github.vkuzel.gradle_dependency_graph.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResourceManager {

    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    @Autowired
    private ProjectDependencyManager projectDependencyManager;

    private final Comparator<ProjectResource> INDEPENDENT_PROJECT_RESOURCES_FIRST = (pr1, pr2) ->
            projectDependencyManager.INDEPENDENT_PROJECT_FIRST.compare(pr1.project, pr2.project);

    public List<ProjectResource> findOnClasspath(String locationPattern) {
        List<ProjectResource> projectResources;

        try {
            projectResources = Arrays.stream(resourceResolver.getResources("classpath*:" + locationPattern))
                    .map(this::createProjectResource).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        projectResources.sort(INDEPENDENT_PROJECT_RESOURCES_FIRST);

        return projectResources;
    }

    public Resource findFirstSpecificFromClasspathOrWorkingDir(String locationPattern) {
        try {
            Resource[] fileResources = resourceResolver.getResources("file:" + locationPattern);
            if (fileResources.length > 0) {
                return fileResources[0];
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        List<ProjectResource> classpathProjectResources = findOnClasspath(locationPattern);
        if (classpathProjectResources.size() > 0) {
            return classpathProjectResources.get(classpathProjectResources.size() - 1).getResource();
        }
        return null;
    }

    private ProjectResource createProjectResource(Resource resource) {
        ProjectResource projectResource = null;

        final URL resourceUrl;
        try {
            resourceUrl = resource.getURL();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        for (Node.Project project : projectDependencyManager.getIndependentProjectsFirst()) {
            String path = resourceUrl.getFile();
            if (ResourceUtils.isJarURL(resourceUrl)) {
                int separatorIndex = path.substring(0, path.lastIndexOf(ResourceUtils.JAR_URL_SEPARATOR))
                        .lastIndexOf(ResourceUtils.JAR_URL_SEPARATOR);
                if (separatorIndex != -1) {
                    path = path.substring(separatorIndex);
                }
            }

            if (path.contains("/" + project.getDir() + "/") || path.contains("/" + project.getDir() + ".jar")) { // FIXME Search for whole path components
                if (projectResource != null) {
                    throw new IllegalStateException("Two projects (" + projectResource.getProjectName() + " and " + project.getName() +
                            ") has been found for resource " + resourceUrl.toString() + "!" +
                            " Please check the name of each module is unique!");
                }
                projectResource = new ProjectResource(project, resource);
            }
        }
        if (projectResource == null) {
            throw new IllegalStateException("Project for resource " + resourceUrl.toString() + " wasn't found in project dependency graph!" +
                    " Please make sure that gradle generateDependencyGraph task has been executed.");
        }
        return projectResource;
    }

    public static class ProjectResource {
        private final Node.Project project;
        private final Resource resource;

        private ProjectResource(Node.Project project, Resource resource) {
            this.project = project;
            this.resource = resource;
        }

        public String getProjectName() {
            return project != null ? project.getName() : null;
        }

        public Resource getResource() {
            return resource;
        }

        public String getResourcePath() {
            try {
                return resource.getURL().getPath();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        public String getResourceFileName() {
            return resource.getFilename();
        }
    }
}
