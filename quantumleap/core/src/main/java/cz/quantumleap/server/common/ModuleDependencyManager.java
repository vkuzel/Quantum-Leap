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
public class ModuleDependencyManager {

    // TODO Each module is going to have it's own dependency file.
    private static final String MODULE_DEPENDENCY_GRAPH_PATH = "/projectDependencyGraph.ser";
    private final PathMatchingResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private final List<Node.Project> independentModulesFirst = new ArrayList<>();
    final Comparator<Node.Project> INDEPENDENT_MODULE_FIRST = (module1, module2) ->
            Integer.compare(independentModulesFirst.indexOf(module1), independentModulesFirst.indexOf(module2));

    public List<Node.Project> getIndependentModulesFirst() {
        return independentModulesFirst;
    }

    public List<String> getModuleNames() {
        return independentModulesFirst.stream()
                .map(Node.Project::getName).collect(Collectors.toList());
    }

    @PostConstruct
    private void loadModuleDependencies() {
        Resource resource = resourceResolver.getResource("classpath:" + MODULE_DEPENDENCY_GRAPH_PATH);
        if (!resource.exists()) {
            throw new IllegalStateException("Module dependency graph file " + MODULE_DEPENDENCY_GRAPH_PATH + " is not found." +
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

        buildModuleList(dependencyGraph);
    }

    private void buildModuleList(Node dependencyGraph) {
        Node.Project module = dependencyGraph.getProject();
        independentModulesFirst.remove(module);

        int furtherChildPosition = dependencyGraph.getChildren().stream()
                .mapToInt(node -> independentModulesFirst.indexOf(node.getProject())).max().orElse(-1);

        independentModulesFirst.add(furtherChildPosition + 1, module);

        dependencyGraph.getChildren().forEach(this::buildModuleList);
    }
}
