package com.github.vkuzel.gradleprojectdependencies;

import java.io.Serializable;
import java.util.List;

import static java.util.Objects.requireNonNull;

public record ModuleDependencies(
        String name,
        String dir,
        boolean root,
        List<String> dependencies
) implements Serializable {

    public ModuleDependencies {
        requireNonNull(name, "Name is null!");
        requireNonNull(dir, "Dir is null!");
        requireNonNull(dependencies, "Dependencies is null!");
    }

    @Override
    public String toString() {
        return "ModuleDependencies{" +
                "name='" + name + '\'' +
                ", dir='" + dir + '\'' +
                ", root=" + root +
                ", dependencies=" + String.join(", ", dependencies) +
                '}';
    }
}