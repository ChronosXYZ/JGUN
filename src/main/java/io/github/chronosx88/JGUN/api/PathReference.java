package io.github.chronosx88.JGUN.api;

import io.github.chronosx88.JGUN.api.graph.NodeBuilder;
import io.github.chronosx88.JGUN.models.GetResult;
import io.github.chronosx88.JGUN.models.Result;
import io.github.chronosx88.JGUN.models.graph.MemoryGraph;
import io.github.chronosx88.JGUN.models.graph.Node;
import io.github.chronosx88.JGUN.models.graph.NodeMetadata;
import io.github.chronosx88.JGUN.models.graph.values.NodeLinkValue;
import io.github.chronosx88.JGUN.models.requests.GetRequestParams;
import io.github.chronosx88.JGUN.network.NetworkManager;
import io.github.chronosx88.JGUN.storage.StorageManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

public class PathReference {
    private final List<String> path = new ArrayList<>();

    private final NetworkManager networkManager;
    private final StorageManager storageManager;

    public PathReference(NetworkManager networkManager, StorageManager storageManager) {
        this.networkManager = networkManager;
        this.storageManager = storageManager;
    }

    public PathReference get(String key) {
        path.add(key);
        return this;
    }

    public CompletableFuture<GetResult> once() {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return storageManager.getPathData(path.toArray(new String[0]));
                    } catch (TimeoutException | ExecutionException | InterruptedException e) {
                        throw new CompletionException(e);
                    }
                })
                .thenComposeAsync(pathData -> {
                    if (pathData.size() < path.size()-1) {
                        return CompletableFuture.completedFuture(GetResult.builder().data(null).build());
                    }
                    String field = null;
                    if (path.size() - pathData.size() == 1) {
                        field = path.get(path.size()-1);
                    }

                    return storageManager.fetchNodeId(GetRequestParams.builder()
                                    .nodeId(pathData.get(pathData.size()-1))
                                    .field(field)
                                    .build());
                });
    }

    public CompletableFuture<Result> put(MemoryGraph graph) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return storageManager.getPathData(path.toArray(new String[0]));
                    } catch (TimeoutException | ExecutionException | InterruptedException e) {
                        throw new CompletionException(e);
                    }
                })
                .thenComposeAsync(pathData -> {
                    String newNodeId = null;
                    if (pathData.size() < path.size()) {
                        String nodeId = pathData.get(pathData.size()-1);
                        int newNodeCount = path.size() - pathData.size();
                        String[] pathNewItems = Arrays.stream(path.toArray(new String[0]), pathData.size(), path.size()).toArray(String[]::new);
                        for (int i = 0; i < newNodeCount; i++) {
                            newNodeId = UUID.randomUUID().toString();
                            graph.putNodes(nodeId, Node.builder()
                                    .metadata(NodeMetadata.builder()
                                            .nodeID(nodeId)
                                            .states(Map.of(pathNewItems[i], System.currentTimeMillis()))
                                            .build())
                                    .values(Map.of(pathNewItems[i], NodeLinkValue.builder()
                                            .link(newNodeId)
                                            .build()))
                                    .build());
                            nodeId = newNodeId;
                        }
                    } else {
                        newNodeId = UUID.randomUUID().toString();
                        if (pathData.size() > 1) {
                            String parentNodeId = pathData.get(pathData.size()-2);
                            graph.putNodes(parentNodeId, Node.builder()
                                    .metadata(NodeMetadata.builder()
                                            .nodeID(parentNodeId)
                                            .states(Map.of(path.get(path.size()-1), System.currentTimeMillis()))
                                            .build())
                                    .values(Map.of(path.get(path.size()-1), NodeLinkValue.builder()
                                            .link(newNodeId)
                                            .build()))
                                    .build());
                        } else {
                            newNodeId = pathData.get(0);
                        }

                    }
                    graph.nodes.get(NodeBuilder.ROOT_NODE).getMetadata().setNodeID(newNodeId);
                    graph.nodes.put(newNodeId, graph.nodes.get(NodeBuilder.ROOT_NODE));
                    graph.nodes.remove(NodeBuilder.ROOT_NODE);
                    return this.storageManager.putData(graph);
                });
    }

    public void on(NodeChangeListener changeListener) {
        storageManager.addChangeListener(String.join("/", path), changeListener);
    }

    public void map(NodeChangeListener.Map forEachListener) {
        storageManager.addMapChangeListener(String.join("/", path), forEachListener);
    }
}
