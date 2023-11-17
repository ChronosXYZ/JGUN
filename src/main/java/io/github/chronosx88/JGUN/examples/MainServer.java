package io.github.chronosx88.JGUN.examples;

import io.github.chronosx88.JGUN.network.GatewayNetworkNode;
import io.github.chronosx88.JGUN.storage.MemoryStorage;

public class MainServer {
    public static void main(String[] args) {
        GatewayNetworkNode gunSuperNode = new GatewayNetworkNode(5054, new MemoryStorage());
        gunSuperNode.start();
    }
}
