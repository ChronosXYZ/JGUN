package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.models.MemoryGraph;
import io.github.chronosx88.JGUN.models.Node;
import io.github.chronosx88.JGUN.models.NodeLink;
import io.github.chronosx88.JGUN.models.NodeMetadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public class GraphNodeBuilder {
    private final MemoryGraph graph;
    private final Node rootNode;
    protected static final String ROOT_NODE = "__ROOT__";

    public GraphNodeBuilder() {
        this.graph = MemoryGraph.builder().build();
        this.rootNode = Node.builder()
                .metadata(NodeMetadata.builder()
                        .nodeID(null)
                        .build())
                .build();
        graph.nodes.put(ROOT_NODE, this.rootNode);
    }

    private GraphNodeBuilder addScalar(String name, Object value) {
        rootNode.values.put(name, value);
        rootNode.getMetadata().getStates().put(name, System.currentTimeMillis());
        return this;
    }

    public GraphNodeBuilder add(String name, String value) {
        return addScalar(name, value);
    }

    public GraphNodeBuilder add(String name, BigInteger value) {
        return addScalar(name, value);
    }

    public GraphNodeBuilder add(String name, BigDecimal value) {
        return addScalar(name, value);
    }

    public GraphNodeBuilder add(String name, int value) {
        return addScalar(name, value);
    }

    public GraphNodeBuilder add(String name, long value) {
        return addScalar(name, value);
    }

    public GraphNodeBuilder add(String name, double value) {
        return addScalar(name, value);
    }

    public GraphNodeBuilder add(String name, boolean value) {
        return addScalar(name, value);

    }

    public GraphNodeBuilder addNull(String name) {
        return addScalar(name, null);
    }

    public GraphNodeBuilder add(String name, GraphNodeBuilder builder) {
        String newNodeID = UUID.randomUUID().toString();
        rootNode.values.put(name, NodeLink.builder()
                .link(newNodeID)
                .build());
        MemoryGraph innerGraph = builder.build();
        innerGraph.nodes.get(ROOT_NODE).getMetadata().setNodeID(newNodeID);
        innerGraph.nodes.put(newNodeID, innerGraph.nodes.get(ROOT_NODE));
        innerGraph.nodes.remove(ROOT_NODE);
        graph.nodes.putAll(innerGraph.nodes);
        return this;
    }

    public GraphNodeBuilder add(String name, NodeArrayBuilder builder) {
        MemoryGraph innerGraph = builder.build();
        //noinspection unchecked
        var innerArray = (List<Object>) innerGraph.nodes.get(ROOT_NODE).values.get(NodeArrayBuilder.ARRAY_FIELD);
        rootNode.values.put(name, innerArray);
        rootNode.getMetadata().getStates().put(name, innerGraph
                .nodes
                .get(ROOT_NODE)
                .getMetadata()
                .getStates()
                .get(NodeArrayBuilder.ARRAY_FIELD));
        innerGraph.nodes.remove(ROOT_NODE);
        graph.nodes.putAll(innerGraph.nodes);
        return this;
    }

    public MemoryGraph build() {
        return this.graph;
    }
}