package io.github.chronosx88.GunJava;

import io.github.chronosx88.GunJava.storageBackends.MemoryBackend;
import io.github.chronosx88.GunJava.storageBackends.StorageBackend;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class Client extends WebSocketClient {
    private Dup dup = new Dup();
    private StorageBackend graph = new MemoryBackend();

    public Client(InetAddress address, int port) throws URISyntaxException {
        super(new URI("ws://" + address.getHostAddress() + ":" + port));
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("Connection open. Status: " + handshakeData.getHttpStatus());
        Utils.setTimeout(() -> {
            JSONObject msg = new JSONObject();
            msg
                    .put("#", dup.track(Dup.random()))
                    .put("get", new JSONObject()
                            .put("#", "FDSA")
                            .put(".", "species"));
            this.send(msg.toString());
        }, 2000);

        Utils.setTimeout(() -> {
            JSONObject msg = new JSONObject();
            msg.put("#", dup.track(Dup.random()));
            msg.put("put", new JSONObject()
                    .put("ASDF", Utils.newNode("ASDF", new JSONObject()
                            .put("name", "Mark Nadal")
                            .put("boss", new JSONObject().put("#", "FDSA"))).toJSONObject())
                    .put("FDSA", Utils.newNode("FDSA", new JSONObject().put("name", "Fluffy").put("species", "a kitty").put("slave", new JSONObject().put("#", "ASDF"))).toJSONObject()));
            this.send(msg.toString());
        }, (int) (1000 * Math.random()));
        Utils.setTimeout(() -> {
            JSONObject msg = new JSONObject();
            msg.put("#", dup.track(Dup.random()));
            msg.put("put", new JSONObject()
                    .put("ASDF", Utils.newNode("ASDF", new JSONObject()
                            .put("name", "Mark")).toJSONObject())
                    .put("FDSA", Utils.newNode("FDSA", new JSONObject().put("species", "felis silvestris").put("color", "ginger")).toJSONObject()));
            this.send(msg.toString());
        }, (int) (1000 * Math.random()));
    }

    @Override
    public void onMessage(String message) {
        JSONObject msg = new JSONObject(message);
        if(dup.check(msg.getString("#"))){ return; }
        dup.track(msg.getString("#"));
        if(msg.opt("put") != null) {
            HAM.mix(new MemoryBackend(msg.getJSONObject("put")), graph);
        }
        if(msg.opt("get") != null) {
            MemoryBackend getResults = Utils.getRequest(msg.getJSONObject("get"), graph);
            JSONObject ack = new JSONObject()
                    .put("#", dup.track(Dup.random()))
                    .put("@", msg.getString("#"))
                    .put("put", getResults.toJSONObject());
            this.send(ack.toString());
        }
        System.out.println("---------------");
        System.out.println(msg.toString(2));
        this.send(message);
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
}
