package io.github.chronosx88.JGUN.api;

import io.github.chronosx88.JGUN.network.NetworkManager;
import io.github.chronosx88.JGUN.network.Peer;
import io.github.chronosx88.JGUN.storage.Storage;
import io.github.chronosx88.JGUN.storage.StorageManager;

public class Gun {
    private final StorageManager storageManager;
    private final NetworkManager networkManager;

    public Gun(Storage storage, Peer peer) throws InterruptedException {
        this.networkManager = new NetworkManager(peer, peer.getNetworkHandler());
        this.storageManager = new StorageManager(storage, this.networkManager);
        this.networkManager.start();
    }

    public PathReference get(String key) {
        PathReference pathRef = new PathReference(networkManager, storageManager);
        pathRef.get(key);
        return pathRef;
    }
}
