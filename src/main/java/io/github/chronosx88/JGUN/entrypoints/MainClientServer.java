package io.github.chronosx88.JGUN.entrypoints;

import io.github.chronosx88.JGUN.nodes.GunPeer;
import io.github.chronosx88.JGUN.nodes.GunSuperPeer;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;

import java.net.Inet4Address;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class MainClientServer {
    public static void main(String[] args) throws URISyntaxException, UnknownHostException {
        GunSuperPeer gunSuperNode = new GunSuperPeer(21334);
        gunSuperNode.start();
        GunPeer gunClient = new GunPeer(Inet4Address.getByAddress(new byte[]{127, 0, 0, 1}), 21334, new InMemoryGraph());
        gunClient.connect();
    }
}
