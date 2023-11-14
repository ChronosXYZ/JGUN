package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.models.MemoryGraph;
import io.github.chronosx88.JGUN.nodes.GunClient;
import io.github.chronosx88.JGUN.storage.Storage;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Gun {
    private GunClient gunClient;
    private final Storage storage;

    public Gun(InetAddress address, int port, Storage storage) {
        this.storage = storage;
        try {
            this.gunClient = new GunClient(address, port, storage);
            this.gunClient.connectBlocking();
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PathReference get(String key) {
        PathReference pathRef = new PathReference(this);
        pathRef.get(key);
        return pathRef;
    }

    protected void addChangeListener(String nodeID, NodeChangeListener listener) {
        storage.addChangeListener(nodeID, listener);
    }

    protected void addMapChangeListener(String nodeID, NodeChangeListener.Map listener) {
        storage.addMapChangeListener(nodeID, listener);
    }

    protected void sendPutRequest(MemoryGraph data) {
        // TODO
    }

    protected void sendGetRequest(String key, String field) {
        // TODO
    }
}
