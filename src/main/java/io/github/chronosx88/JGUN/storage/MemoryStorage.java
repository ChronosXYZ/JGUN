package io.github.chronosx88.JGUN.storage;

import io.github.chronosx88.JGUN.models.Node;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class MemoryStorage extends Storage {
    private final Map<String, Node> nodes;

    public MemoryStorage()     {
        nodes = new LinkedHashMap<>();
    }

    public Node getNode(String id) {
        return nodes.get(id);
    }

    @Override
    void updateNode(Node node) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public void addNode(String id, Node incomingNode) {
        nodes.put(id, incomingNode);
    }

    public boolean hasNode(String id) {
        return nodes.containsKey(id);
    }

    public Set<Map.Entry<String, Node>> entries() {
        return nodes.entrySet();
    }

    public Collection<Node> nodes() {
        return nodes.values();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
