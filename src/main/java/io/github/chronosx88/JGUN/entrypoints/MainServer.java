package io.github.chronosx88.JGUN.entrypoints;

import io.github.chronosx88.JGUN.nodes.GunSuperPeer;

public class MainServer {
    public static void main(String[] args) {
        GunSuperPeer gunSuperNode = new GunSuperPeer(5054);
        gunSuperNode.start();
    }
}
