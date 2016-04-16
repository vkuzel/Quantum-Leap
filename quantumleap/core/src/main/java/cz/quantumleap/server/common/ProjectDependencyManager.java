package cz.quantumleap.server.common;

import com.github.vkuzel.gradle_dependency_graph.Node;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProjectDependencyManager {

    private static final String PROJECT_DEPENDENCY_GRAPH_PATH = "/projectDependencyGraph.ser";
    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private final List<Node.Project> independentProjectsFirst = new ArrayList<>();
    final Comparator<Node.Project> INDEPENDENT_PROJECT_FIRST = (project1, project2) ->
            Integer.compare(independentProjectsFirst.indexOf(project1), independentProjectsFirst.indexOf(project2));

    public List<Node.Project> getIndependentProjectsFirst() {
        return independentProjectsFirst;
    }

    public List<String> getProjectNames() {
        return independentProjectsFirst.stream()
                .map(Node.Project::getName).collect(Collectors.toList());
    }

    @PostConstruct
    private void loadProjectDependencies() {
        Resource resource = resourceResolver.getResource("classpath:" + PROJECT_DEPENDENCY_GRAPH_PATH);
        if (!resource.exists()) {
            throw new IllegalStateException("Project dependency graph file " + PROJECT_DEPENDENCY_GRAPH_PATH + " is not found." +
                    " Make sure that `gradle generateDependencyGraph` task has been executed and dependencyGraphPath build property is properly configured.");
        }

        Node dependencyGraph;
        try (
                InputStream inputStream = resource.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
        ) {
            dependencyGraph = (Node) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

        buildProjectList(dependencyGraph);
    }

    private void buildProjectList(Node dependencyGraph) {
        Node.Project project = dependencyGraph.getProject();
        independentProjectsFirst.remove(project);

        int furtherChildPosition = dependencyGraph.getChildren().stream()
                .mapToInt(node -> independentProjectsFirst.indexOf(node.getProject())).max().orElse(-1);

        independentProjectsFirst.add(furtherChildPosition + 1, project);

        dependencyGraph.getChildren().forEach(this::buildProjectList);
    }
}
