package io.github.chronosx88.JGUN.nodes;

import io.github.chronosx88.JGUN.Dup;
import io.github.chronosx88.JGUN.HAM;
import io.github.chronosx88.JGUN.Utils;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;

public class GunSuperPeer extends WebSocketServer implements Peer {
    private Dup dup = new Dup();
    private InMemoryGraph graph = new InMemoryGraph();

    public GunSuperPeer(int port) {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Connected new peer: " + conn.getRemoteSocketAddress().toString());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Peer " + conn.getRemoteSocketAddress().toString() + " closed the connection for reason (code): " + reason + " (" + code + ")");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JSONObject msg = new JSONObject(message);
        if(dup.check(msg.getString("#"))) { return; }
        dup.track(msg.getString("#"));
        if(msg.opt("put") != null) {
            HAM.mix(new InMemoryGraph(msg.getJSONObject("put")), graph);
        }
        if(msg.opt("get") != null) {
            InMemoryGraph result = Utils.getRequest(msg.optJSONObject("get"), graph);
            JSONObject ack = new JSONObject();
            if(!result.isEmpty()) {
                emit(ack
                        .put("#", dup.track(Dup.random()))
                        .put("@", msg.getString("#"))
                        .put("put", result.toJSONObject())
                        .toString());
            } else {
                emit(ack
                        .put("#", dup.track(Dup.random()))
                        .put("@", msg.getString("#"))
                        .put("ok", false)
                        .toString());
            }
        }
        emit(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("# Exception occured on connection: " + conn.getRemoteSocketAddress());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("GunSuperPeer started on port: " + getPort());
    }

    public void emit(String data) {
        for(WebSocket conn : this.getConnections()) {
            conn.send(data);
        }
    }
}
