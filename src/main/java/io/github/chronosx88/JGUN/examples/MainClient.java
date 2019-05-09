package io.github.chronosx88.JGUN.examples;

import io.github.chronosx88.JGUN.nodes.GunClient;
import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;

import java.net.Inet4Address;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

public class MainClient {
    public static void main(String[] args) throws URISyntaxException, UnknownHostException {
        GunClient gunClient = new GunClient(Inet4Address.getByAddress(new byte[]{127, 0, 0, 1}), 5054, new InMemoryGraph());
        gunClient.connect();
    }
}
