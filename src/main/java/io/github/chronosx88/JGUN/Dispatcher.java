package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.BaseCompletableFuture;
import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;
import io.github.chronosx88.JGUN.nodes.Peer;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import io.github.chronosx88.JGUN.storageBackends.StorageBackend;
import org.java_websocket.client.WebSocketClient;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dispatcher {
    private final Map<String, BaseCompletableFuture<?>> pendingFutures = new ConcurrentHashMap<>();
    private final Map<String, NodeChangeListener> changeListeners = new HashMap<>();
    private final Peer peer;
    private final StorageBackend graphStorage;
    private final Dup dup;

    public Dispatcher(StorageBackend graphStorage, Peer peer, Dup dup) {
        this.graphStorage = graphStorage;
        this.peer = peer;
        this.dup = dup;
    }

    public void addPendingFuture(BaseCompletableFuture<?> future) {
        pendingFutures.put(future.getFutureID(), future);
    }

    public void handleIncomingMessage(JSONObject message) {
        if(message.has("put")) {
            JSONObject ack = handlePut(message);
            peer.emit(ack.toString());
        }
        if(message.has("await")) {
            JSONObject ack = handleGet(message);
            peer.emit(ack.toString());
        }
        if(message.has("@")) {
            handleIncomingAck(message);
        }
        peer.emit(message.toString());
    }

    private JSONObject handleGet(JSONObject getData) {
        InMemoryGraph getResults = Utils.getRequest(getData.getJSONObject("await"), graphStorage);
        return new JSONObject() // Acknowledgment
                .put( "#", dup.track(Dup.random()) )
                .put( "@", getData.getString("#") )
                .put( "put", getResults.toJSONObject() )
                .put( "ok", !(getResults.isEmpty()) );
    }

    private JSONObject handlePut(JSONObject message) {
        InMemoryGraph diff = HAM.mix(new InMemoryGraph(message.getJSONObject("put")), graphStorage);
        if(diff != null) {
            for(Map.Entry<String, Node> entry : diff.entries()) {
                if(changeListeners.containsKey(entry.getKey())) {
                    changeListeners.get(entry.getKey()).onChange(entry.getValue().toUserJSONObject());
                }
            }
        }
        return new JSONObject() // Acknowledgment
                .put( "#", dup.track(Dup.random()) )
                .put( "@", message.getString("#") )
                .put( "ok", true);
    }

    private void handleIncomingAck(JSONObject ack) {
        if(ack.has("put")) {
            if(pendingFutures.containsKey(ack.getString("@"))) {
                BaseCompletableFuture<?> future = pendingFutures.get(ack.getString("@"));
                if(future instanceof FutureGet) {
                    ((FutureGet) future).complete(new InMemoryGraph(ack.getJSONObject("put")).toUserJSONObject());
                }
            }
        }
        if(ack.has("ok")) {
            if(pendingFutures.containsKey(ack.getString("@"))) {
                BaseCompletableFuture<?> future = pendingFutures.get(ack.getString("@"));
                if(future instanceof FuturePut) {
                    ((FuturePut) future).complete(ack.getBoolean("ok"));
                }
            }
        }
    }

    public void sendPutRequest(String messageID, JSONObject data) {
        new Thread(() -> {
            InMemoryGraph graph = Utils.prepareDataForPut(data);
            peer.emit(Utils.formatPutRequest(messageID, graph.toJSONObject()).toString());
        }).start();
    }

    public void sendGetRequest(String messageID, String key, String field) {
        new Thread(() -> {
            JSONObject jsonGet = Utils.formatGetRequest(messageID, key, field);
            peer.emit(jsonGet.toString());
        }).start();
    }

    public void addChangeListener(String soul, NodeChangeListener listener) {
        changeListeners.put(soul, listener);
    }
}
