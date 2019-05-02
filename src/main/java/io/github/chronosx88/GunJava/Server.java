package io.github.chronosx88.GunJava;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

public class Server extends WebSocketServer {
    private Timer timer = new Timer(true);

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
                conn.send("hello world " + count[0]);
            }
        }, 0, 1000);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        //
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("received: " + message);
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
