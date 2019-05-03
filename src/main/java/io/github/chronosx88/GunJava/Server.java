package io.github.chronosx88.GunJava;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Timer;

public class Server extends WebSocketServer {
    private Timer timer = new Timer(true);
    private Dup dup = new Dup();
    private ArrayList<WebSocket> peers = new ArrayList<>();
    private Graph graph = new Graph();

    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        peers.add(conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        //
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JSONObject msg = new JSONObject(message);
        if(dup.check(msg.getString("#"))) { return; }
        dup.track(msg.getString("#"));
        if(msg.opt("put") != null) {
            HAM.mix(new Graph(msg.getJSONObject("put")), graph);
            System.out.println("----------------");
            System.out.println(graph.toPrettyString());
        }
        for (WebSocket peer : peers) {
            peer.send(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //
    }

    @Override
    public void onStart() {
        //
    }
}
