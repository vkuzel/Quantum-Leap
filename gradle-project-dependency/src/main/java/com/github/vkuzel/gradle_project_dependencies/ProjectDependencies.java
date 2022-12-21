package com.github.vkuzel.gradle_project_dependencies;

import java.io.Serializable;
import java.util.List;

public class ProjectDependencies implements Serializable {

    private final String name;
    private final String dir;
    private final boolean root;
    private final List<String> dependencies;

    public ProjectDependencies(String name, String dir, boolean root, List<String> dependencies) {
        if (name == null) {
            throw new NullPointerException("Name has to be not null!");
        } else if (dir == null) {
            throw new NullPointerException("Dir has to be not null!");
        } else if (dependencies == null) {
            throw new NullPointerException("Dependencies has to be not null!");
        }

        this.name = name;
        this.dir = dir;
        this.root = root;
        this.dependencies = dependencies;
    }

    public String getName() {
        return name;
    }

    public String getDir() {
        return dir;
    }

    public boolean isRoot() {
        return root;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "ProjectDependencies{" +
                "name='" + name + '\'' +
                ", dir='" + dir + '\'' +
                ", root=" + root +
                ", dependencies=" + String.join(", ", dependencies) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectDependencies projectDependencies = (ProjectDependencies) o;

        if (root != projectDependencies.root) return false;
        if (!name.equals(projectDependencies.name)) return false;
        if (!dir.equals(projectDependencies.dir)) return false;
        return dependencies.equals(projectDependencies.dependencies);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + dir.hashCode();
        result = 31 * result + (root ? 1 : 0);
        result = 31 * result + dependencies.hashCode();
        return result;
    }
}