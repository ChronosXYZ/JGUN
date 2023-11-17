package io.github.chronosx88.JGUN.api.graph;

import io.github.chronosx88.JGUN.models.graph.*;
import io.github.chronosx88.JGUN.models.graph.values.*;
import io.github.chronosx88.JGUN.models.graph.NodeValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public class NodeBuilder {
    private final MemoryGraph graph;
    private final Node rootNode;
    public static final String ROOT_NODE = "__ROOT__";

    public NodeBuilder() {
        this.graph = new MemoryGraph();
        this.rootNode = Node.builder()
                .metadata(NodeMetadata.builder()
                        .nodeID(null)
                        .build())
                .build();
        graph.nodes.put(ROOT_NODE, this.rootNode);
    }

    private NodeBuilder addScalar(String name, NodeValue value) {
        rootNode.values.put(name, value);
        rootNode.getMetadata().getStates().put(name, System.currentTimeMillis());
        return this;
    }

    public NodeBuilder add(String name, String value) {
        return addScalar(name, new StringValue(value));
    }

    public NodeBuilder add(String name, BigInteger value) {
        return addScalar(name, new IntValue(value));
    }

    public NodeBuilder add(String name, BigDecimal value) {
        return addScalar(name, new DecimalValue(value));
    }

    public NodeBuilder add(String name, int value) {
        return addScalar(name, new IntValue(value));
    }

    public NodeBuilder add(String name, long value) {
        return addScalar(name, new IntValue(value));
    }

    public NodeBuilder add(String name, double value) {
        return addScalar(name, new DecimalValue(value));
    }

    public NodeBuilder add(String name, boolean value) {
        return addScalar(name, new BooleanValue(value));
    }

    public NodeBuilder addNull(String name) {
        return addScalar(name, null);
    }

    public NodeBuilder add(String name, NodeBuilder builder) {
        String newNodeID = UUID.randomUUID().toString();
        rootNode.values.put(name, NodeLinkValue.builder()
                .link(newNodeID)
                .build());
        rootNode.getMetadata().getStates().put(name, System.currentTimeMillis());
        MemoryGraph innerGraph = builder.build();
        innerGraph.nodes.get(ROOT_NODE).getMetadata().setNodeID(newNodeID);
        innerGraph.nodes.put(newNodeID, innerGraph.nodes.get(ROOT_NODE));
        innerGraph.nodes.remove(ROOT_NODE);
        graph.nodes.putAll(innerGraph.nodes);
        return this;
    }

    public NodeBuilder add(String name, ArrayBuilder builder) {
        MemoryGraph innerGraph = builder.build();
        var innerArray = (NodeValue) innerGraph.nodes.get(ROOT_NODE).values.get(ArrayBuilder.ARRAY_FIELD);
        rootNode.values.put(name, innerArray);
        rootNode.getMetadata().getStates().put(name, innerGraph
                .nodes
                .get(ROOT_NODE)
                .getMetadata()
                .getStates()
                .get(ArrayBuilder.ARRAY_FIELD));
        innerGraph.nodes.remove(ROOT_NODE);
        graph.nodes.putAll(innerGraph.nodes);
        return this;
    }

    public MemoryGraph build() {
        return this.graph;
    }
}