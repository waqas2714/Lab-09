package graph;

import java.util.*;

/**
 * A mutable, weighted, directed graph with labeled vertices.
 *
 * @param <L> type of vertex labels in this graph, must be immutable
 */
public class ConcreteGraph<L> implements Graph<L> {

    // Internal representation of the graph: adjacency list
    private final Map<L, Map<L, Integer>> adjacencyList = new HashMap<>();

    /**
     * Create an empty graph.
     *
     * @param <L> type of vertex labels in the graph, must be immutable
     * @return a new empty weighted directed graph
     */
    public static <L> Graph<L> empty() {
        return new ConcreteGraph<>();
    }

    @Override
    public boolean add(L vertex) {
        if (adjacencyList.containsKey(vertex)) {
            return false; // Vertex already exists
        }
        adjacencyList.put(vertex, new HashMap<>());
        return true;
    }

    @Override
    public int set(L source, L target, int weight) {
        // Add the vertices if they do not already exist
        add(source);
        add(target);

        Map<L, Integer> edges = adjacencyList.get(source);
        int previousWeight = edges.getOrDefault(target, 0);

        if (weight == 0) {
            // Remove the edge if weight is zero
            edges.remove(target);
        } else {
            // Add or update the edge with the new weight
            edges.put(target, weight);
        }

        return previousWeight;
    }

    @Override
    public boolean remove(L vertex) {
        if (!adjacencyList.containsKey(vertex)) {
            return false; // Vertex does not exist
        }

        // Remove the vertex from adjacency list
        adjacencyList.remove(vertex);

        // Remove all edges pointing to the vertex
        for (Map<L, Integer> edges : adjacencyList.values()) {
            edges.remove(vertex);
        }

        return true;
    }

    @Override
    public Set<L> vertices() {
        return Collections.unmodifiableSet(adjacencyList.keySet());
    }

    @Override
    public Map<L, Integer> sources(L target) {
        Map<L, Integer> sources = new HashMap<>();
        for (Map.Entry<L, Map<L, Integer>> entry : adjacencyList.entrySet()) {
            L source = entry.getKey();
            Map<L, Integer> edges = entry.getValue();

            if (edges.containsKey(target)) {
                sources.put(source, edges.get(target));
            }
        }
        return Collections.unmodifiableMap(sources);
    }

    @Override
    public Map<L, Integer> targets(L source) {
        if (!adjacencyList.containsKey(source)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(adjacencyList.get(source));
    }

    @Override
    public String toString() {
        return adjacencyList.toString();
    }
}
