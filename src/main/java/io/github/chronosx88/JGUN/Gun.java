package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.nodes.GunClient;
import io.github.chronosx88.JGUN.storage.Storage;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Gun {
    private GunClient gunClient;
    private final Map<String, NodeChangeListener> changeListeners = new ConcurrentHashMap<>();
    private final Map<String, NodeChangeListener.Map> mapChangeListeners = new ConcurrentHashMap<>();

    public Gun(InetAddress address, int port, Storage storage) {
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
        changeListeners.put(nodeID, listener);
    }

    protected void addMapChangeListener(String nodeID, NodeChangeListener.Map listener) {
        mapChangeListeners.put(nodeID, listener);
    }
}
