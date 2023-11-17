package io.github.chronosx88.JGUN.storage;

import io.github.chronosx88.JGUN.api.FuturePut;
import io.github.chronosx88.JGUN.api.NodeChangeListener;
import io.github.chronosx88.JGUN.models.GetResult;
import io.github.chronosx88.JGUN.models.graph.MemoryGraph;
import io.github.chronosx88.JGUN.models.graph.Node;
import io.github.chronosx88.JGUN.models.graph.NodeValue;
import io.github.chronosx88.JGUN.models.graph.values.NodeLinkValue;
import io.github.chronosx88.JGUN.models.requests.GetRequestParams;
import io.github.chronosx88.JGUN.network.NetworkManager;
import io.github.chronosx88.JGUN.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class StorageManager {
    private final Storage storage;
    private final NetworkManager networkManager;
    private final Executor executorService = Executors.newCachedThreadPool();

    public StorageManager(Storage storage, NetworkManager networkManager) {
        this.storage = storage;
        this.networkManager = networkManager;
    }

    public void addChangeListener(String nodeID, NodeChangeListener listener) {
        storage.addChangeListener(nodeID, listener);
    }

    public void addMapChangeListener(String nodeID, NodeChangeListener.Map listener) {
        storage.addMapChangeListener(nodeID, listener);
    }

    public void mergeUpdate(MemoryGraph update) {
        executorService.execute(() -> {
            this.storage.mergeUpdate(update);
        });
    }

    public List<String> getPathData(String[] path) throws TimeoutException, ExecutionException, InterruptedException {
        List<String> nodeIds = new ArrayList<>();
        String nodeId = path[0];
        for (int i = 0; i < path.length; i++) {
            String field = null;
            if (i+1 < path.length) {
                field = path[i+1];
            }
            Node node = storage.getNode(nodeId, field);
            if (node != null) {
                if (field == null) {
                    nodeIds.add(nodeId);
                    break;
                }
                if (node.values.containsKey(field) && node.values.get(field).getValueType() == NodeValue.ValueType.LINK) {
                    nodeId = ((NodeLinkValue) node.values.get(field)).getLink();
                    nodeIds.add(nodeId);
                    continue;
                }
            }
            // proceeds to request from the network
            var future = this.networkManager.sendGetRequest(GetRequestParams.builder()
                            .nodeId(nodeId)
                            .field(field)
                            .build());
            var result = future.get(networkManager.getTimeout(), TimeUnit.SECONDS);
            if (result.getData() == null || field == null) {
                nodeIds.add(nodeId);
                break;
            }
            if (result.getData().values.containsKey(field) && result.getData().values.get(field).getValueType() == NodeValue.ValueType.LINK) {
                nodeId = ((NodeLinkValue) result.getData().values.get(field)).getLink();
                nodeIds.add(nodeId);
            }
        }
        return nodeIds;
    }

    public FuturePut putData(MemoryGraph graph) {
        this.storage.mergeUpdate(graph);
        return this.networkManager.sendPutRequest(graph);
    }

    public CompletableFuture<GetResult> fetchNodeId(GetRequestParams params) {
        return CompletableFuture.supplyAsync(() -> this.storage.getNode(params.getNodeId(), params.getField()))
                .thenCompose(node -> {
                    if (node != null) {
                        return CompletableFuture.completedFuture(GetResult.builder()
                                .ok(true)
                                .data(node)
                                .build());
                    }
                    return networkManager.sendGetRequest(params);
                });
    }
}
