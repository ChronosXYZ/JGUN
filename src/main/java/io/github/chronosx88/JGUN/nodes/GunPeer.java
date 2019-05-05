package io.github.chronosx88.JGUN.nodes;

import io.github.chronosx88.JGUN.*;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import io.github.chronosx88.JGUN.storageBackends.StorageBackend;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class GunPeer extends WebSocketClient implements Peer {
    private Dup dup = new Dup();
    private final StorageBackend storage;
    private final Dispatcher dispatcher;

    public GunPeer(InetAddress address, int port, StorageBackend storage) throws URISyntaxException {
        super(new URI("ws://" + address.getHostAddress() + ":" + port));
        this.storage = storage;
        this.dispatcher = new Dispatcher(storage, this);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("# Connection with SuperNode open. Status: " + handshakeData.getHttpStatus());
    }

    @Override
    public void onMessage(String message) {
        JSONObject msg = new JSONObject(message);
        if(dup.check(msg.getString("#"))){ return; }
        dup.track(msg.getString("#"));
        if(msg.opt("put") != null) {
            HAM.mix(new InMemoryGraph(msg.getJSONObject("put")), storage);
        }
        if(msg.opt("get") != null) {
            InMemoryGraph getResults = Utils.getRequest(msg.getJSONObject("get"), storage);
            JSONObject ack = new JSONObject()
                    .put("#", dup.track(Dup.random()))
                    .put("@", msg.getString("#"))
                    .put("put", getResults.toJSONObject());
            emit(ack.toString());
        }
        System.out.println("---------------");
        System.out.println(msg.toString(2));
        emit(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed. Code/reason/remote: " + code + "/" + reason + "/" + remote);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Terrible fail: ");
        ex.printStackTrace();
    }

    @Override
    public void emit(String data) {
        this.send(data);
    }

    public PathRef get(String key) {
        PathRef pathRef = new PathRef(dispatcher);
        pathRef.get(key);
        return pathRef;
    }
}
