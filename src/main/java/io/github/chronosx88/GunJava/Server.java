package io.github.chronosx88.GunJava;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.Timer;

public class Server extends WebSocketServer {
    private Timer timer = new Timer(true);
    private Dup dup = new Dup();
    private Graph graph = new Graph();

    public Server(int port) {
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
            HAM.mix(new Graph(msg.getJSONObject("put")), graph);
        }
        if(msg.opt("get") != null) {
            Graph result = Utils.getRequest(msg.optJSONObject("get"), graph);
            if(!result.isEmpty()) {
                JSONObject ack = new JSONObject();
                emit(ack
                        .put("#", dup.track(Dup.random()))
                        .put("@", msg.getString("#"))
                        .put("put", result.toJSONObject())
                        .toString());
            }
        }
        emit(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //
    }

    @Override
    public void onStart() {
        System.out.println("Server started on port: " + getPort());
    }

    public void emit(String data) {
        for(WebSocket conn : this.getConnections()) {
            conn.send(data);
        }
    }
}
