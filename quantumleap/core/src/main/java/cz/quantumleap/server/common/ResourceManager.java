package cz.quantumleap.server.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

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
    private ModuleDependencyManager moduleDependencyManager;

    private final Comparator<ResourceWithModule> INDEPENDENT_MODULE_RESOURCES_FIRST = (mr1, mr2) ->
            moduleDependencyManager.INDEPENDENT_MODULE_FIRST.compare(mr1.module, mr2.module);

    public List<ResourceWithModule> findOnClasspath(String locationPattern) {
        List<ResourceWithModule> resourceWithModules;

        try {
            resourceWithModules = Arrays.stream(resourceResolver.getResources("classpath*:" + locationPattern))
                    .map(this::createResourceWithModule).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        resourceWithModules.sort(INDEPENDENT_MODULE_RESOURCES_FIRST);

        return resourceWithModules;
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
        List<ResourceWithModule> classpathResourceWithModules = findOnClasspath(locationPattern);
        if (classpathResourceWithModules.size() > 0) {
            return classpathResourceWithModules.get(classpathResourceWithModules.size() - 1).getResource();
        }
        return null;
    }

    private ResourceWithModule createResourceWithModule(Resource resource) {
        ResourceWithModule resourceWithModule = null;

        final URL resourceUrl;
        try {
            resourceUrl = resource.getURL();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        for (ModuleDependencyManager.Dependencies module : moduleDependencyManager.getIndependentModulesFirst()) {
            if (module.isInModule(resourceUrl)) {
                if (resourceWithModule != null) {
                    throw new IllegalStateException("Two modules (" + resourceWithModule.getModuleName() + " and " + module.getModuleName() +
                            ") has been found for resource " + resourceUrl.toString() + "!" +
                            " The name of each module has to be unique!");
                }
                resourceWithModule = new ResourceWithModule(module, resource);
            }
        }
        if (resourceWithModule == null) {
            throw new IllegalStateException("No module has not been found for resource " + resourceUrl.toString() +
                    " Please make sure that gradle discoverProjectDependencies task has been executed.");
        }
        return resourceWithModule;
    }

    public static class ResourceWithModule {
        private final ModuleDependencyManager.Dependencies module;
        private final Resource resource;

        public ResourceWithModule(ModuleDependencyManager.Dependencies module, Resource resource) {
            this.module = module;
            this.resource = resource;
        }

        public String getModuleName() {
            return module.getModuleName();
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
