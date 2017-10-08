package cz.quantumleap.core.resource;

import cz.quantumleap.core.module.ModuleDependencies;
import org.springframework.core.io.Resource;

import java.io.IOException;

public class ResourceWithModule {
    private final ModuleDependencies moduleDependencies;
    private final Resource resource;

    public ResourceWithModule(ModuleDependencies moduleDependencies, Resource resource) {
        this.moduleDependencies = moduleDependencies;
        this.resource = resource;
    }

    public String getModuleName() {
        return moduleDependencies.getModuleName();
    }

    public ModuleDependencies getModuleDependencies() {
        return moduleDependencies;
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
}
