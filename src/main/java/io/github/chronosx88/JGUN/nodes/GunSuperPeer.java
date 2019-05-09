package io.github.chronosx88.JGUN.nodes;

import io.github.chronosx88.JGUN.Dispatcher;
import io.github.chronosx88.JGUN.Dup;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;

public class GunSuperPeer extends WebSocketServer implements Peer {
    private Dup dup = new Dup();
    private InMemoryGraph graph = new InMemoryGraph();
    private Dispatcher dispatcher;

    public GunSuperPeer(int port) {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        dispatcher = new Dispatcher(graph, this, dup);
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
        JSONObject jsonMsg = new JSONObject(message);
        if(dup.check(jsonMsg.getString("#"))){ return; }
        dup.track(jsonMsg.getString("#"));
        dispatcher.handleIncomingMessage(jsonMsg);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if(conn != null) {
            System.out.println("# Exception occured on connection: " + conn.getRemoteSocketAddress());
        }
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
