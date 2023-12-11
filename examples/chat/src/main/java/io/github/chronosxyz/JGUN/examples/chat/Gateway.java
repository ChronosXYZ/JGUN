package io.github.chronosxyz.JGUN.examples.chat;

import io.github.chronosx88.JGUN.api.Gun;
import io.github.chronosx88.JGUN.network.GatewayNetworkNode;
import io.github.chronosx88.JGUN.storage.MemoryStorage;
import io.github.chronosx88.JGUN.storage.Storage;

public class Gateway {
    public static void main(String[] args) throws InterruptedException {
        Storage storage = new MemoryStorage();
        GatewayNetworkNode peer = new GatewayNetworkNode(5054, storage);
        Gun gun = new Gun(storage, peer);
    }
}
