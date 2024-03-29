package io.github.chronosx88.JGUN.storage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.github.chronosx88.JGUN.models.graph.DeferredNode;
import io.github.chronosx88.JGUN.models.graph.Node;
import io.github.chronosx88.JGUN.models.graph.NodeMetadata;
import io.github.chronosx88.JGUN.models.graph.NodeValue;
import io.github.chronosx88.JGUN.models.graph.values.ArrayValue;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class MemoryStorage extends Storage {
    private final Map<String, Node> nodes = new ConcurrentHashMap<>();
    private final Cache<String, DeferredNode> deferredNodes;

    public MemoryStorage() {
        deferredNodes = Caffeine.newBuilder().expireAfter(new Expiry<String, DeferredNode>() {
                    @Override
                    public long expireAfterCreate(String key, DeferredNode value, long currentTime) {
                        return value.getDelay(TimeUnit.NANOSECONDS);
                    }

                    @Override
                    public long expireAfterUpdate(String key, DeferredNode value, long currentTime, @NonNegative long currentDuration) {
                        return Long.MAX_VALUE;
                    }

                    @Override
                    public long expireAfterRead(String key, DeferredNode value, long currentTime, @NonNegative long currentDuration) {
                        return Long.MAX_VALUE;
                    }
                })
                .evictionListener((key, value, cause) -> {
                    assert value != null;
                    this.mergeNode(value, System.currentTimeMillis());
                }).build();
    }

    public Node getNode(String id, String field) {
        Node node = nodes.get(id);
        if (node != null && field != null) {
            NodeValue requestedField = node.getValues().get(field);
            if (requestedField != null) {
                node = Node.builder()
                        .metadata(NodeMetadata.builder()
                                .nodeID(node.getMetadata().getNodeID())
                                .states(Map.of(field, node.getMetadata().getStates().get(field)))
                                .build())
                        .values(Map.of(field, requestedField))
                        .build();
            }
        }
        return node;
    }

    @Override
    protected void updateNode(Node newNode) {
        Node currentNode = nodes.get(newNode.getMetadata().getNodeID());
        newNode.values.forEach((k, v) -> {
            if (v.getValueType() != NodeValue.ValueType.ARRAY) return;
            var currentValue = currentNode.getValues().get(k);
            if (currentValue.getValueType() != NodeValue.ValueType.ARRAY) return;
            ArrayValue currentArrayValue = (ArrayValue) currentValue;
            currentArrayValue.getValue().addAll(((ArrayValue) v).getValue());
            newNode.getValues().put(k, currentArrayValue);
        });
        currentNode.values.putAll(newNode.values);
        currentNode.getMetadata().getStates().putAll(newNode.getMetadata().getStates());
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

    @Override
    protected void putDeferredNode(DeferredNode node) {
        deferredNodes.put(node.getMetadata().getNodeID(), node);
    }
}
