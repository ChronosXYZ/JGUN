package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.nodes.GunClient;
import io.github.chronosx88.JGUN.storageBackends.StorageBackend;

import java.net.InetAddress;
import java.net.URISyntaxException;

public class Gun {
    private Dispatcher dispatcher;
    private GunClient gunClient;

    public Gun(InetAddress address, int port, StorageBackend storage) {
        try {
            this.gunClient = new GunClient(address, port, storage);
            this.dispatcher = gunClient.getDispatcher();
            this.gunClient.connectBlocking();
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public PathRef get(String key) {
        PathRef pathRef = new PathRef(dispatcher);
        pathRef.get(key);
        return pathRef;
    }
}
