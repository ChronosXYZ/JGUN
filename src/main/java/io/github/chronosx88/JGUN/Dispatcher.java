package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.BaseFuture;
import io.github.chronosx88.JGUN.nodes.Peer;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import io.github.chronosx88.JGUN.storageBackends.StorageBackend;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dispatcher {
    private final Map<String, BaseFuture<? extends BaseFuture>> pendingFutures = new ConcurrentHashMap<>();
    private final StorageBackend graphStorage;
    private final Peer peer;

    public Dispatcher(StorageBackend graphStorage, Peer peer) {
        this.graphStorage = graphStorage;
        this.peer = peer;
    }

    public void addPendingFuture(BaseFuture<? extends BaseFuture> future) {
        pendingFutures.put(future.getFutureID(), future);
    }

    public void handleIncomingMessage(JSONObject message) {
        // FIXME
    }

    private JSONObject handleGet(JSONObject getData) {
        return null; // FIXME
    }

    private JSONObject handlePut(JSONObject putData) {
        return null; // FIXME
    }

    private void handleIncomingAck(JSONObject ack) {
        // FIXME
    }

    public void sendPutRequest(JSONObject data) {
        new Thread(() -> {
            InMemoryGraph graph = Utils.prepareDataForPut(data);
            peer.emit(Utils.formatPutRequest(graph.toJSONObject()).toString());
        }).start();
    }

    public void sendGetRequest(String key) {
        new Thread(() -> {
            JSONObject jsonGet = Utils.formatGetRequest(key, null);
            peer.emit(jsonGet.toString());
        }).start();
    }

    public void sendGetRequest(String key, String field) {
        new Thread(() -> {
            JSONObject jsonGet = Utils.formatGetRequest(key, field);
            peer.emit(jsonGet.toString());
        }).start();
    }
}
