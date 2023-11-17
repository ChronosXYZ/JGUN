package io.github.chronosx88.JGUN.network;

import io.github.chronosx88.JGUN.api.FutureGet;
import io.github.chronosx88.JGUN.api.FuturePut;
import io.github.chronosx88.JGUN.storage.Storage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class GatewayNetworkNode extends WebSocketServer implements Peer {
    private final NetworkHandler handler;

    public GatewayNetworkNode(int port, Storage storage) {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        Dup dup = new Dup(1000 * 9);
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
        handler.handleIncomingMessage(message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if(conn != null) {
            System.out.println("# Exception occurred on connection: " + conn.getRemoteSocketAddress());
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

    @Override
    public void addPendingPutRequest(FuturePut futurePut) {
        this.handler.addPendingPutRequest(futurePut);
    }

    @Override
    public void addPendingGetRequest(FutureGet futureGet) {
        this.handler.addPendingGetRequest(futureGet);
    }

    @Override
    public int getTimeout() {
        return 60;
    }

    @Override
    public int connectedPeerCount() {
        return this.getConnections().size();
    }
}
