package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.BaseFuture;
import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;
import io.github.chronosx88.JGUN.nodes.Peer;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import io.github.chronosx88.JGUN.storageBackends.StorageBackend;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Dispatcher {
    private final Map<String, BaseFuture<? extends BaseFuture>> pendingFutures = new ConcurrentHashMap<>();
    private final StorageBackend graphStorage;
    private final Dup dup;
    private final Peer peer;

    public Dispatcher(StorageBackend graphStorage, Peer peer, Dup dup) {
        this.graphStorage = graphStorage;
        this.peer = peer;
        this.dup = dup;
    }

    public void addPendingFuture(BaseFuture<? extends BaseFuture> future) {
        pendingFutures.put(future.getFutureID(), future);
    }

    public void handleIncomingMessage(JSONObject message) {
        if(message.has("put")) {
            JSONObject ack = handlePut(message);
            peer.emit(ack.toString());
        }
        if(message.has("get")) {
            JSONObject ack = handleGet(message);
            peer.emit(ack.toString());
        }
        if(message.has("@")) {
            handleIncomingAck(message);
        }
    }

    private JSONObject handleGet(JSONObject getData) {
        InMemoryGraph getResults = Utils.getRequest(getData.getJSONObject("get"), graphStorage);
        return new JSONObject() // Acknowledgment
                .put( "#", dup.track(Dup.random()) )
                .put( "@", getData.getString("#") )
                .put( "put", getResults.toJSONObject() )
                .put( "ok", !(getResults.isEmpty()) );
    }

    private JSONObject handlePut(JSONObject message) {
        HAM.mix(new InMemoryGraph(message.getJSONObject("put")), graphStorage);
        return new JSONObject() // Acknowledgment
                .put( "#", dup.track(Dup.random()) )
                .put( "@", message.getString("#") )
                .put( "ok", true);
    }

    private void handleIncomingAck(JSONObject ack) {
        if(ack.has("put")) {
            if(pendingFutures.containsKey(ack.getString("@"))) {
                BaseFuture<? extends BaseFuture> future = pendingFutures.get(ack.getString("@"));
                if(future instanceof FutureGet) {
                    ((FutureGet) future).done(ack.getJSONObject("put"));
                }
            }
        }
        if(ack.has("ok")) {
            if(pendingFutures.containsKey(ack.getString("@"))) {
                BaseFuture<? extends BaseFuture> future = pendingFutures.get(ack.getString("@"));
                if(future instanceof FuturePut) {
                    ((FuturePut) future).done(ack.getBoolean("ok"));
                }
            }
        }
    }

    public void sendPutRequest(JSONObject data) {
        new Thread(() -> {
            InMemoryGraph graph = Utils.prepareDataForPut(data);
            peer.emit(Utils.formatPutRequest(graph.toJSONObject()).toString());
        }).start();
    }

    public void sendGetRequest(String key, String field) {
        new Thread(() -> {
            JSONObject jsonGet = Utils.formatGetRequest(key, field);
            peer.emit(jsonGet.toString());
        }).start();
    }
}
