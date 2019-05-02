package io.github.chronosx88.GunJava;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException {
        Server server = new Server(5054);
        server.start();
        WebSocketClient client = new WebSocketClient(new URI("ws://127.0.0.1:5054")) {
            @Override
            public void onOpen(ServerHandshake handshakeData) {
                System.out.println("open " + handshakeData.getHttpStatusMessage());
            }

            @Override
            public void onMessage(String message) {
                System.out.println(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("close " + code + " " + reason + " " + remote);
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("error");
                ex.printStackTrace();
            }
        };
        client.connect();
    }
}
