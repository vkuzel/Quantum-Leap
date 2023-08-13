package cz.quantumleap.core.resource;

import cz.quantumleap.core.module.ModuleDependencyManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ResourceManager {

    private static final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private final ModuleDependencyManager moduleDependencyManager;

    public ResourceManager(ModuleDependencyManager moduleDependencyManager) {
        this.moduleDependencyManager = moduleDependencyManager;
    }

    public List<ResourceWithModule> findInClasspath(String locationPattern) {
        List<ResourceWithModule> resourceWithModules;

        try {
            resourceWithModules = Arrays.stream(resourceResolver.getResources("classpath*:" + locationPattern))
                    .map(this::createResourceWithModule).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        resourceWithModules.sort(this::independentModuleResourcesFirst);

        return resourceWithModules;
    }

    private int independentModuleResourcesFirst(ResourceWithModule mr1, ResourceWithModule mr2) {
        return moduleDependencyManager.independentModuleFirst().compare(mr1.getModuleDependencies(), mr2.getModuleDependencies());
    }

    public Optional<Resource> findMostSpecificInClasspathOrWorkingDir(String locationPattern) {
        Resource resource = null;
        try {
            var fileResources = resourceResolver.getResources("file:" + locationPattern);
            if (fileResources.length > 0 && fileResources[0].exists()) {
                resource = fileResources[0];
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        if (resource == null) {
            var classpathResourceWithModules = findInClasspath(locationPattern);
            if (classpathResourceWithModules.size() > 0) {
                resource = classpathResourceWithModules.get(classpathResourceWithModules.size() - 1).getResource();
            }
        }
        return Optional.ofNullable(resource);
    }

    private ResourceWithModule createResourceWithModule(Resource resource) {
        ResourceWithModule resourceWithModule = null;
        for (var module : moduleDependencyManager.getIndependentModulesFirst()) {
            if (module.containsResource(resource)) {
                if (resourceWithModule != null) {
                    throw new IllegalStateException("Two modules (" + resourceWithModule.getModuleName() + " and " + module.getModuleName() +
                            ") has been found for resource " + resourceToString(resource) + "!" +
                            " The name of each module has to be unique!");
                }
                resourceWithModule = new ResourceWithModule(module, resource);
            }
        }
        if (resourceWithModule == null) {
            throw new IllegalStateException("No module has been found for resource " + resourceToString(resource) +
                    " Please make sure that gradle generateModuleDependencies task has been executed.");
        }
        return resourceWithModule;
    }

    private String resourceToString(Resource resource) {
        try {
            return resource.getURL().toString();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
