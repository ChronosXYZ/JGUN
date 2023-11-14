package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.BaseCompletableFuture;
import io.github.chronosx88.JGUN.models.BaseMessage;
import io.github.chronosx88.JGUN.models.MemoryGraph;
import io.github.chronosx88.JGUN.models.acks.BaseAck;
import io.github.chronosx88.JGUN.models.acks.GetAck;
import io.github.chronosx88.JGUN.models.acks.PutAck;
import io.github.chronosx88.JGUN.models.requests.GetRequest;
import io.github.chronosx88.JGUN.models.requests.PutRequest;
import io.github.chronosx88.JGUN.nodes.Peer;
import io.github.chronosx88.JGUN.storage.Storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkHandler {
    private final Map<String, BaseCompletableFuture<?>> pendingFutures = new ConcurrentHashMap<>();

    private final Peer peer;
    private final Storage graphStorage;
    private final Dup dup;
    private final Executor executorService = Executors.newCachedThreadPool();

    public NetworkHandler(Storage graphStorage, Peer peer, Dup dup) {
        this.graphStorage = graphStorage;
        this.peer = peer;
        this.dup = dup;
    }

    public void addPendingFuture(BaseCompletableFuture<?> future) {
        pendingFutures.put(future.getFutureID(), future);
    }

    public void handleIncomingMessage(BaseMessage message) {
        if (message instanceof GetRequest) {
            handleGet((GetRequest) message);
        } else if (message instanceof PutRequest) {
            handlePut((PutRequest) message);
        } else if (message instanceof BaseAck) {
            handleAck((BaseAck) message);
        }
        peer.emit(message.toString());
    }

    private GetAck handleGet(GetRequest request) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    private PutAck handlePut(PutRequest request) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    private void handleAck(BaseAck ack) {
        if (ack instanceof GetAck) {
            // TODO
        } else if (ack instanceof PutAck) {
            // TODO
        }

        throw new UnsupportedOperationException("TODO");
    }

    public void sendPutRequest(String messageID, MemoryGraph data) {
        executorService.execute(() -> {
            // TODO
        });
    }

    public void sendGetRequest(String messageID, String key, String field) {
        executorService.execute(() -> {
            // TODO
        });
    }
}
