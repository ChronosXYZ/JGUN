package io.github.chronosx88.JGUN.nodes;

import io.github.chronosx88.JGUN.Dup;
import io.github.chronosx88.JGUN.NetworkHandler;
import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;
import io.github.chronosx88.JGUN.storage.Storage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class GunClient extends WebSocketClient implements Peer {
    private Dup dup = new Dup(1000*9);
    private final NetworkHandler handler;

    public GunClient(InetAddress address, int port, Storage storage) throws URISyntaxException {
        super(new URI("ws://" + address.getHostAddress() + ":" + port));
        this.handler = new NetworkHandler(storage, this, dup);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("# Connection with SuperNode open. Status: " + handshakeData.getHttpStatus());
    }

    @Override
    public void onMessage(String message) {
        handler.handleIncomingMessage(message);
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

    @Override
    public void emit(String data) {
        this.send(data);
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
    public void start() {
        this.connect();
    }
}
