package io.github.chronosx88.GunJava;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class Server extends WebSocketServer {
    private Timer timer = new Timer(true);
    private Dup dup = new Dup();

    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        final int[] count = {0};
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                count[0] += 1;
                JSONObject msg = new JSONObject();
                msg.put("#", dup.track(String.valueOf(count[0])));
                conn.send(msg.toString());
            }
        }, 0, 1000);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        //
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JSONObject msg = new JSONObject(message);
        //if(dup.check(msg.getString("#"))) { return; }
        dup.track(msg.getString("#"));
        System.out.println("received: " + msg.toString());
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
