package io.github.chronosx88.JGUN.storage;

import io.github.chronosx88.JGUN.HAM;
import io.github.chronosx88.JGUN.NodeChangeListener;
import io.github.chronosx88.JGUN.models.DeferredNode;
import io.github.chronosx88.JGUN.models.MemoryGraph;
import io.github.chronosx88.JGUN.models.Node;
import io.github.chronosx88.JGUN.models.NodeMetadata;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class Storage {
    abstract Node getNode(String id);

    abstract void updateNode(Node node);

    abstract void addNode(String id, Node node);

    abstract boolean hasNode(String id);

    abstract Set<Map.Entry<String, Node>> entries();

    abstract Collection<Node> nodes();

    abstract boolean isEmpty();

    abstract void putDeferredNode(DeferredNode node);

    /**
     * Merge graph update (usually received from the network)
     *
     * @param update             Graph update
     * @param changeListeners    User callbacks which fired when Node has changed (.on() API)
     * @param mapChangeListeners User callbacks which fired when Node has changed (.map() API)
     */
    public void mergeUpdate(MemoryGraph update, Map<String, NodeChangeListener> changeListeners, Map<String, NodeChangeListener.Map> mapChangeListeners) {
        long machine = System.currentTimeMillis();
        MemoryGraph diff = new MemoryGraph();
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
                    changeListeners.get(diffEntry.getKey()).onChange(diffEntry.getValue());
                }
                if (mapChangeListeners.containsKey(diffEntry.getKey())) {
                    for (Map.Entry<String, Object> nodeEntry : changedNode.getValues().entrySet()) {
                        mapChangeListeners.get(nodeEntry.getKey()).onChange(nodeEntry.getKey(), nodeEntry.getValue());
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
}
