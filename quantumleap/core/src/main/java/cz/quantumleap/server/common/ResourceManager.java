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
    private ModuleDependencyManager moduleDependencyManager;

    private final Comparator<ModuleResource> INDEPENDENT_MODULE_RESOURCES_FIRST = (mr1, mr2) ->
            moduleDependencyManager.INDEPENDENT_MODULE_FIRST.compare(mr1.module, mr2.module);

    public List<ModuleResource> findOnClasspath(String locationPattern) {
        List<ModuleResource> moduleResources;

        try {
            moduleResources = Arrays.stream(resourceResolver.getResources("classpath*:" + locationPattern))
                    .map(this::createModuleResource).collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        moduleResources.sort(INDEPENDENT_MODULE_RESOURCES_FIRST);

        return moduleResources;
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
        List<ModuleResource> classpathModuleResources = findOnClasspath(locationPattern);
        if (classpathModuleResources.size() > 0) {
            return classpathModuleResources.get(classpathModuleResources.size() - 1).getResource();
        }
        return null;
    }

    private ModuleResource createModuleResource(Resource resource) {
        ModuleResource moduleResource = null;

        final URL resourceUrl;
        try {
            resourceUrl = resource.getURL();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        for (Node.Project module : moduleDependencyManager.getIndependentModulesFirst()) {
            String path = resourceUrl.getFile();
            if (ResourceUtils.isJarURL(resourceUrl)) {
                int separatorIndex = path.substring(0, path.lastIndexOf(ResourceUtils.JAR_URL_SEPARATOR))
                        .lastIndexOf(ResourceUtils.JAR_URL_SEPARATOR);
                if (separatorIndex != -1) {
                    path = path.substring(separatorIndex);
                }
            }

            if (path.contains("/quantumleap/" + module.getDir() + "/") || path.contains("/" + module.getDir() + ".jar")) { // FIXME Search for whole path components
                if (moduleResource != null) {
                    throw new IllegalStateException("Two modules (" + moduleResource.getModuleName() + " and " + module.getName() +
                            ") has been found for resource " + resourceUrl.toString() + "!" +
                            " The name of each module has to be unique!");
                }
                moduleResource = new ModuleResource(module, resource);
            }
        }
        if (moduleResource == null) {
            throw new IllegalStateException("Module for resource " + resourceUrl.toString() + " wasn't found in module dependency graph!" +
                    " Please make sure that gradle generateDependencyGraph task has been executed.");
        }
        return moduleResource;
    }

    public static class ModuleResource {
        private final Node.Project module;
        private final Resource resource;

        private ModuleResource(Node.Project module, Resource resource) {
            this.module = module;
            this.resource = resource;
        }

        public String getModuleName() {
            return module != null ? module.getName() : null;
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
