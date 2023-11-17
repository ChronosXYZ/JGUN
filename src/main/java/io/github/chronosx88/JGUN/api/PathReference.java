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
import java.util.concurrent.*;
import java.util.stream.Stream;

public class PathReference {
    private final List<String> path = new ArrayList<>();

    private final NetworkManager networkManager;
    private final StorageManager storageManager;
    private final Executor executorService = Executors.newCachedThreadPool();

    public PathReference(NetworkManager networkManager, StorageManager storageManager) {
        this.networkManager = networkManager;
        this.storageManager = storageManager;
    }

    public String[] getPath() {
        return path.toArray(new String[0]);
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
                        graph.nodes.get(NodeBuilder.ROOT_NODE).getMetadata().setNodeID(newNodeId);
                        graph.nodes.put(newNodeId, graph.nodes.get(NodeBuilder.ROOT_NODE));
                        graph.nodes.remove(NodeBuilder.ROOT_NODE);
                    } else {
                        // merge updated node under parent ID
                        String parentNodeId = pathData.get(pathData.size()-1);
                        graph.nodes.get(NodeBuilder.ROOT_NODE).getMetadata().setNodeID(parentNodeId);
                        graph.nodes.put(parentNodeId, graph.nodes.get(NodeBuilder.ROOT_NODE));
                    }
                    graph.nodes.remove(NodeBuilder.ROOT_NODE);
                    return this.storageManager.putData(graph);
                });
    }

    public void on(NodeChangeListener changeListener) {
        executorService.execute(() -> {
            List<String> pathData;
            try {
                pathData = storageManager.getPathData(path.toArray(new String[0]));
            } catch (TimeoutException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            storageManager.addChangeListener(pathData.get(pathData.size()-1), changeListener);
        });
    }

    public void map(NodeChangeListener.Map mapListener) {
        executorService.execute(() -> {
            List<String> pathData;
            try {
                pathData = storageManager.getPathData(path.toArray(new String[0]));
            } catch (TimeoutException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            storageManager.addMapChangeListener(pathData.get(pathData.size()-1), mapListener);
        });
    }

    public CompletableFuture<Result> put(PathReference nodeReference) {
        String[] pathToAnotherNode = nodeReference.getPath();
        return CompletableFuture.supplyAsync(() -> {
            try {
                return storageManager.getPathData(pathToAnotherNode);
            } catch (TimeoutException | ExecutionException | InterruptedException e) {
                throw new CompletionException(e);
            }
        }).thenComposeAsync(pathData -> {
            if (pathData.size() < pathToAnotherNode.length) {
                return CompletableFuture.failedFuture(new IllegalArgumentException("target node not found"));
            }
            String nodeId = pathData.get(pathData.size()-1);
            MemoryGraph graph = new NodeBuilder()
                    .add(path.get(path.size() - 1), NodeLinkValue.builder()
                            .link(nodeId)
                            .build())
                    .build();
            if (path.size() > 1) this.path.remove(path.size()-1);
            return this.put(graph);
        });
    }
}
