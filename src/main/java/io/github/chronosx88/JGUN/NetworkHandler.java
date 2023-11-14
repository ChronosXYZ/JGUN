package io.github.chronosx88.JGUN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;
import io.github.chronosx88.JGUN.futures.GetResult;
import io.github.chronosx88.JGUN.futures.Result;
import io.github.chronosx88.JGUN.models.BaseMessage;
import io.github.chronosx88.JGUN.models.MemoryGraph;
import io.github.chronosx88.JGUN.models.Node;
import io.github.chronosx88.JGUN.models.NodeMetadata;
import io.github.chronosx88.JGUN.models.acks.BaseAck;
import io.github.chronosx88.JGUN.models.acks.GetAck;
import io.github.chronosx88.JGUN.models.requests.GetRequest;
import io.github.chronosx88.JGUN.models.requests.PutRequest;
import io.github.chronosx88.JGUN.nodes.Peer;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NetworkHandler(Storage storage, Peer peer, Dup dup) {
        this.storage = storage;
        this.peer = peer;
        this.dup = dup;
    }

    public void addPendingGetRequest(FutureGet future) {
        this.pendingGetRequests.put(future.getFutureID(), future);
    }

    public void addPendingPutRequest(FuturePut future) {
        this.pendingPutRequests.put(future.getFutureID(), future);
    }

    public void handleIncomingMessage(String message) {
        BaseMessage parsedMessage;
        try {
            parsedMessage = objectMapper.readValue(message, BaseMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (dup.isDuplicated(parsedMessage.getId())) {
            // TODO log
            return;
        }

        final BaseMessage msg = parsedMessage;
        executorService.execute(() -> {
            BaseMessage response = null;
            if (msg instanceof GetRequest) {
                response = handleGet((GetRequest) msg);
            } else if (msg instanceof PutRequest) {
                response = handlePut((PutRequest) msg);
            } else if (msg instanceof BaseAck) {
                response = handleAck((BaseAck) msg);
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
        Node node = storage.getNode(request.getParams().getNodeID());
        if (Objects.isNull(node)) return null;
        String fieldName = request.getParams().getField();
        if (Objects.nonNull(fieldName)) {
            Object fieldValue = node.getValues().get(fieldName);
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
        return GetAck.builder()
                .id(Dup.random())
                .replyTo(request.getId())
                .data(MemoryGraph.builder()
                        .nodes(Map.of(node.getMetadata().getNodeID(), node))
                        .build())
                .ok(true)
                .build();
    }

    private BaseAck handlePut(PutRequest request) {
        storage.mergeUpdate(request.getGraph());
        return BaseAck.builder()
                .id(Dup.random())
                .replyTo(request.getId())
                .ok(true)
                .build();
    }

    private BaseAck handleAck(BaseAck ack) {
        if (ack instanceof GetAck) {
            FutureGet future = pendingGetRequests.get(ack.getReplyTo());
            if (Objects.nonNull(future)) {
                GetAck getAck = (GetAck) ack;
                future.complete(GetResult.builder()
                        .ok(getAck.isOk())
                        .data(getAck.getData())
                        .build());
            }
            return handlePut(PutRequest
                    .builder()
                    .graph(((GetAck) ack).getData())
                    .build());
        } else {
            FuturePut future = pendingPutRequests.get(ack.getReplyTo());
            if (Objects.nonNull(future)) {
                future.complete(Result.builder()
                        .ok(ack.isOk())
                        .build());
            }
            System.out.println("Got ack! { #: '" + ack.getId() + "', @: '" + ack.getReplyTo() + "' }");
            return null;
        }
    }
}
