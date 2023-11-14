package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.models.MemoryGraph;
import io.github.chronosx88.JGUN.models.Node;
import io.github.chronosx88.JGUN.models.NodeLink;
import io.github.chronosx88.JGUN.models.NodeMetadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class NodeArrayBuilder {
    private final MemoryGraph graph;
    private final Node rootNode;
    private final List<Object> innerArray;
    protected static final String ARRAY_FIELD = "__ARRAY__";

    public NodeArrayBuilder() {
        this.graph = MemoryGraph.builder().build();
        this.innerArray = new ArrayList<>();
        this.rootNode = Node.builder()
                .metadata(NodeMetadata.builder()
                        .nodeID(null)
                        .states(new HashMap<>(Map.of(ARRAY_FIELD, System.currentTimeMillis())))
                        .build())
                .values(Map.of(ARRAY_FIELD, innerArray))
                .build();
        graph.nodes.put(GraphNodeBuilder.ROOT_NODE, this.rootNode);
    }

    private NodeArrayBuilder addScalar(Object value) {
        this.innerArray.add(value);
        this.rootNode.getMetadata().getStates().put(ARRAY_FIELD, System.currentTimeMillis());
        return this;
    }

    public NodeArrayBuilder add(String value) {
        return addScalar(value);
    }

    public NodeArrayBuilder add(BigInteger value) {
        return addScalar(value);
    }

    public NodeArrayBuilder add(BigDecimal value) {
        return addScalar(value);
    }

    public NodeArrayBuilder add(int value) {
        return addScalar(value);
    }

    public NodeArrayBuilder add(long value) {
        return addScalar(value);
    }

    public NodeArrayBuilder add(double value) {
        return addScalar(value);
    }

    public NodeArrayBuilder add(boolean value) {
        return addScalar(value);

    }

    public NodeArrayBuilder addNull(String name) {
        return addScalar(null);
    }

    public NodeArrayBuilder add(GraphNodeBuilder builder) {
        String newNodeID = UUID.randomUUID().toString();
        //noinspection unchecked
        List<Object> innerArray = (List<Object>) rootNode.values.get(ARRAY_FIELD);
        innerArray.add(NodeLink.builder()
                .link(newNodeID)
                .build());
        MemoryGraph innerGraph = builder.build();
        innerGraph.nodes.get(GraphNodeBuilder.ROOT_NODE).getMetadata().setNodeID(newNodeID);
        innerGraph.nodes.put(newNodeID, innerGraph.nodes.get(GraphNodeBuilder.ROOT_NODE));
        innerGraph.nodes.remove(GraphNodeBuilder.ROOT_NODE);
        this.graph.nodes.putAll(innerGraph.nodes);
        return this;
    }

    public MemoryGraph build() {
        return this.graph;
    }
}
