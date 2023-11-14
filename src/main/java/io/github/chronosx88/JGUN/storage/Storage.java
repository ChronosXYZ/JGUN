package io.github.chronosx88.JGUN.storage;

import io.github.chronosx88.JGUN.HAM;
import io.github.chronosx88.JGUN.NodeChangeListener;
import io.github.chronosx88.JGUN.models.DeferredNode;
import io.github.chronosx88.JGUN.models.MemoryGraph;
import io.github.chronosx88.JGUN.models.Node;
import io.github.chronosx88.JGUN.models.NodeMetadata;

import java.util.*;

public abstract class Storage {
    public abstract Node getNode(String id);

    protected abstract void updateNode(Node node);

    public abstract void addNode(String id, Node node);

    public abstract boolean hasNode(String id);

    public abstract Set<Map.Entry<String, Node>> entries();

    public  abstract Collection<Node> nodes();

    public abstract boolean isEmpty();

    protected abstract void putDeferredNode(DeferredNode node);

    private final Map<String, List<NodeChangeListener>> changeListeners = new HashMap<>();
    private final Map<String, List<NodeChangeListener.Map>> mapChangeListeners = new HashMap<>();

    /**
     * Merge graph update (usually received from the network)
     *
     * @param update Graph update
     */
    public void mergeUpdate(MemoryGraph update) {
        long machine = System.currentTimeMillis();
        MemoryGraph diff = MemoryGraph.builder().build();
        for (Map.Entry<String, Node> entry : update.getNodes().entrySet()) {
            Node node = entry.getValue();
            Node diffNode = this.mergeNode(node, machine);
            if (Objects.nonNull(diffNode)) {
                diff.nodes.put(diffNode.getMetadata().getNodeID(), diffNode);
            }
        }
        if (!diff.nodes.isEmpty()) {
            for (Map.Entry<String, Node> diffEntry : diff.getNodes().entrySet()) {
                Node changedNode = diffEntry.getValue();
                if (!this.hasNode(changedNode.getMetadata().getNodeID())) {
                    this.addNode(changedNode.getMetadata().getNodeID(), changedNode);
                } else {
                    this.updateNode(changedNode);
                }

                if (changeListeners.containsKey(diffEntry.getKey())) {
                    changeListeners.get(diffEntry.getKey()).forEach((e) -> e.onChange(diffEntry.getValue()));
                }
                if (mapChangeListeners.containsKey(diffEntry.getKey())) {
                    for (Map.Entry<String, Object> nodeEntry : changedNode.getValues().entrySet()) {
                        mapChangeListeners.get(nodeEntry.getKey()).forEach((e) -> e.onChange(nodeEntry.getKey(), nodeEntry.getValue()));
                    }
                }
            }
        }
    }

    /**
     * Merge updated node
     *
     * @param incomingNode Updated node
     * @param machineState Current machine state
     * @return Node with changes or null if no changes
     */
    public Node mergeNode(Node incomingNode, long machineState) {
        Node changedNode = null;
        for (String key : incomingNode.getValues().keySet()) {
            Object value = incomingNode.getValues().get(key);
            long state = incomingNode.getMetadata().getStates().get(key);
            long previousState = -1;
            Object currentValue = null;
            if (this.hasNode(incomingNode.getMetadata().getNodeID())) {
                Node currentNode = this.getNode(incomingNode.getMetadata().getNodeID());
                Long prevStateFromStorage = currentNode.getMetadata().getStates().get(key);
                if (!Objects.isNull(prevStateFromStorage)) {
                    previousState = prevStateFromStorage;
                }
                currentValue = currentNode.getValues().get(key);
            }
            HAM.HAMResult ham = HAM.ham(machineState, state, previousState, value, currentValue);

            if (!ham.incoming) {
                if (ham.defer) {
                    DeferredNode deferred = (DeferredNode) incomingNode;
                    deferred.setDelay(state - machineState);
                    this.putDeferredNode(deferred);
                }
                continue;
            }

            if (Objects.isNull(changedNode)) {
                changedNode = Node.builder()
                        .metadata(NodeMetadata.builder()
                                .nodeID(incomingNode.getMetadata().getNodeID())
                                .build())
                        .build();
            }
            changedNode.values.put(key, value);
            changedNode.getMetadata().getStates().put(key, state);
        }

        return changedNode;
    }

    public void addChangeListener(String nodeID, NodeChangeListener listener) {
        this.changeListeners.putIfAbsent(nodeID, new ArrayList<>());
        this.changeListeners.get(nodeID).add(listener);
    }

    public void clearChangeListeners(String nodeID) {
        this.changeListeners.remove(nodeID);
    }

    public void addMapChangeListener(String nodeID, NodeChangeListener.Map listener) {
        this.mapChangeListeners.putIfAbsent(nodeID, new ArrayList<>());
        this.mapChangeListeners.get(nodeID).add(listener);
    }

    public void clearMapChangeListeners(String nodeID) {
        this.mapChangeListeners.remove(nodeID);
    }
}
