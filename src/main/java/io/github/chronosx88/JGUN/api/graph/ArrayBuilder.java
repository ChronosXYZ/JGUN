package io.github.chronosx88.JGUN.api.graph;

import io.github.chronosx88.JGUN.models.graph.MemoryGraph;
import io.github.chronosx88.JGUN.models.graph.Node;
import io.github.chronosx88.JGUN.models.graph.NodeValue;
import io.github.chronosx88.JGUN.models.graph.values.*;
import io.github.chronosx88.JGUN.models.graph.NodeMetadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class ArrayBuilder {
    private final MemoryGraph graph;
    private final Node rootNode;
    private final List<NodeValue> innerArray;
    protected static final String ARRAY_FIELD = "__ARRAY__";

    public ArrayBuilder() {
        this.graph = new MemoryGraph();
        this.innerArray = new ArrayList<>();
        this.rootNode = Node.builder()
                .metadata(NodeMetadata.builder()
                        .nodeID(null)
                        .states(new HashMap<>(Map.of(ARRAY_FIELD, System.currentTimeMillis())))
                        .build())
                .values(Map.of(ARRAY_FIELD, new ArrayValue(this.innerArray)))
                .build();
        graph.nodes.put(NodeBuilder.ROOT_NODE, this.rootNode);
    }

    private ArrayBuilder addScalar(NodeValue value) {
        this.innerArray.add(value);
        this.rootNode.getMetadata().getStates().put(ARRAY_FIELD, System.currentTimeMillis());
        return this;
    }

    public ArrayBuilder add(String value) {
        return addScalar(new StringValue(value));
    }

    public ArrayBuilder add(BigInteger value) {
        return addScalar(new IntValue(value));
    }

    public ArrayBuilder add(BigDecimal value) {
        return addScalar(new DecimalValue(value));
    }

    public ArrayBuilder add(int value) {
        return addScalar(new IntValue(value));
    }

    public ArrayBuilder add(long value) {
        return addScalar(new IntValue(value));
    }

    public ArrayBuilder add(double value) {
        return addScalar(new DecimalValue(value));
    }

    public ArrayBuilder add(boolean value) {
        return addScalar(new BooleanValue(value));
    }

    public ArrayBuilder addNull(String name) {
        return addScalar(null);
    }

    public ArrayBuilder add(NodeBuilder builder) {
        String newNodeID = UUID.randomUUID().toString();
        List<NodeValue> innerArray = ((ArrayValue) rootNode.values.get(ARRAY_FIELD)).getValue();
        innerArray.add(NodeLinkValue.builder()
                .link(newNodeID)
                .build());
        MemoryGraph innerGraph = builder.build();
        innerGraph.nodes.get(NodeBuilder.ROOT_NODE).getMetadata().setNodeID(newNodeID);
        innerGraph.nodes.put(newNodeID, innerGraph.nodes.get(NodeBuilder.ROOT_NODE));
        innerGraph.nodes.remove(NodeBuilder.ROOT_NODE);
        this.graph.nodes.putAll(innerGraph.nodes);
        return this;
    }

    public MemoryGraph build() {
        return this.graph;
    }
}
