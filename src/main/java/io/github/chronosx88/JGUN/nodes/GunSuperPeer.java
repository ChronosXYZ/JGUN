package io.github.chronosx88.JGUN.nodes;

import io.github.chronosx88.JGUN.Dup;
import io.github.chronosx88.JGUN.NetworkHandler;
import io.github.chronosx88.JGUN.storage.Storage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class GunSuperPeer extends WebSocketServer implements Peer {
    private Dup dup = new Dup(1000*9);
    private NetworkHandler handler;

    public GunSuperPeer(int port, Storage storage) {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        handler = new NetworkHandler(storage, this, dup);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Connected new peer: " + conn.getRemoteSocketAddress().toString());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        if(conn != null) {
            System.out.println("Peer " + conn.getRemoteSocketAddress().toString() + " closed the connection for reason (code): " + reason + " (" + code + ")");
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // TODO
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
