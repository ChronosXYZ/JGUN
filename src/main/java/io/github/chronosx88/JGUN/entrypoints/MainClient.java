package io.github.chronosx88.JGUN.entrypoints;

import io.github.chronosx88.JGUN.nodes.GunPeer;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;

import java.net.Inet4Address;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class MainClient {
    public static void main(String[] args) throws URISyntaxException, UnknownHostException {
        GunPeer gunClient = new GunPeer(Inet4Address.getByAddress(new byte[]{127, 0, 0, 1}), 5054, new InMemoryGraph());
        gunClient.connect();
    }
}
