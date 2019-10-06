package io.github.chronosx88.JGUN.nodes;

import io.github.chronosx88.JGUN.Dispatcher;
import io.github.chronosx88.JGUN.Dup;
import io.github.chronosx88.JGUN.storageBackends.StorageBackend;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class GunClient extends WebSocketClient implements Peer {
    private Dup dup = new Dup();
    private final Dispatcher dispatcher;

    public GunClient(InetAddress address, int port, StorageBackend storage) throws URISyntaxException {
        super(new URI("ws://" + address.getHostAddress() + ":" + port));
        this.dispatcher = new Dispatcher(storage, this, dup);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("# Connection with SuperNode open. Status: " + handshakeData.getHttpStatus());
    }

    @Override
    public void onMessage(String message) {
        JSONObject jsonMsg = new JSONObject(message);
        if(dup.check(jsonMsg.getString("#"))){ return; }
        dup.track(jsonMsg.getString("#"));
        dispatcher.handleIncomingMessage(jsonMsg);
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

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public void emit(String data) {
        this.send(data);
    }
}
