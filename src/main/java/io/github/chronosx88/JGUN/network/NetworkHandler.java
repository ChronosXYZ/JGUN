package io.github.chronosx88.JGUN.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.chronosx88.JGUN.api.FutureGet;
import io.github.chronosx88.JGUN.api.FuturePut;
import io.github.chronosx88.JGUN.models.GetResult;
import io.github.chronosx88.JGUN.models.Result;
import io.github.chronosx88.JGUN.models.NetworkMessage;
import io.github.chronosx88.JGUN.models.graph.MemoryGraph;
import io.github.chronosx88.JGUN.models.graph.Node;
import io.github.chronosx88.JGUN.models.graph.NodeMetadata;
import io.github.chronosx88.JGUN.models.acks.Ack;
import io.github.chronosx88.JGUN.models.acks.GetAck;
import io.github.chronosx88.JGUN.models.graph.NodeValue;
import io.github.chronosx88.JGUN.models.requests.GetRequest;
import io.github.chronosx88.JGUN.models.requests.PutRequest;
import io.github.chronosx88.JGUN.storage.Storage;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkHandler {
    private final Map<String, FutureGet> pendingGetRequests = new ConcurrentHashMap<>();
    private final Map<String, FuturePut> pendingPutRequests = new ConcurrentHashMap<>();

    private final Peer peer;
    private final Storage storage;
    private final Dup dup;
    private final Executor executorService = Executors.newCachedThreadPool();
    private final ObjectMapper objectMapper;

    public NetworkHandler(Storage storage, Peer peer, Dup dup) {
        this.storage = storage;
        this.peer = peer;
        this.dup = dup;

        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
    }

    public void addPendingGetRequest(FutureGet future) {
        this.pendingGetRequests.put(future.getFutureID(), future);
    }

    public void addPendingPutRequest(FuturePut future) {
        this.pendingPutRequests.put(future.getFutureID(), future);
    }

    public void handleIncomingMessage(String message) {
        NetworkMessage parsedMessage;
        try {
            parsedMessage = objectMapper.readValue(message, NetworkMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (dup.isDuplicated(parsedMessage.getId())) {
            // TODO log
            return;
        }

        final NetworkMessage msg = parsedMessage;
        executorService.execute(() -> {
            NetworkMessage response = null;
            if (msg instanceof GetRequest) {
                response = handleGet((GetRequest) msg);
            } else if (msg instanceof PutRequest) {
                response = handlePut((PutRequest) msg);
            } else if (msg instanceof Ack) {
                handleAck((Ack) msg);
            } else if (msg instanceof GetAck) {
                var ack = (GetAck) msg;
                handleGetAck(ack.getData(), ack);
            }
            if (Objects.nonNull(response)) {
                String respString;
                try {
                    respString = objectMapper.writeValueAsString(response);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                peer.emit(respString);
            }
        });
        peer.emit(message);
    }

    private GetAck handleGet(GetRequest request) {
        Node node = storage.getNode(request.getParams().getNodeId(), request.getParams().getField());
        if (Objects.isNull(node)) return GetAck.builder()
                .id(Dup.random())
                .replyTo(request.getId())
                .data(new MemoryGraph())
                .ok(true)
                .build();
        String fieldName = request.getParams().getField();
        if (Objects.nonNull(fieldName)) {
            NodeValue fieldValue = node.values.get(fieldName);
            if (Objects.nonNull(fieldValue)) {
                node = Node.builder()
                        .values(Map.of(fieldName, fieldValue))
                        .metadata(NodeMetadata.builder()
                                .nodeID(node.getMetadata().getNodeID())
                                .states(Map.of(fieldName, node.getMetadata().getStates().get(fieldName)))
                                .build())
                        .build();
            }
        }
        MemoryGraph data = new MemoryGraph();
        data.nodes = Map.of(node.getMetadata().getNodeID(), node);
        return GetAck.builder()
                .id(Dup.random())
                .replyTo(request.getId())
                .data(data)
                .ok(true)
                .build();
    }

    private Ack handlePut(PutRequest request) {
        storage.mergeUpdate(request.getGraph());

        return Ack.builder()
                .id(Dup.random())
                .replyTo(request.getId())
                .ok(true)
                .build();
    }

    private void handleGetAck(MemoryGraph graph, GetAck ack) {
        storage.mergeUpdate(graph);
        FutureGet future = pendingGetRequests.get(ack.getReplyTo());
        if (future != null) {
            GetAck getAck = (GetAck) ack;
            Node node = storage.getNode(future.getParams().getNodeId(), future.getParams().getField());
            future.complete(GetResult.builder()
                    .ok(getAck.isOk())
                    .data(node)
                    .build());
        }
    }

    private void handleAck(Ack ack) {
        FuturePut future = pendingPutRequests.get(ack.getReplyTo());
        if (Objects.nonNull(future)) {
            future.complete(Result.builder()
                    .ok(ack.isOk())
                    .build());
        }
        System.out.println("Got ack! { #: '" + ack.getId() + "', @: '" + ack.getReplyTo() + "' }");
    }
}
