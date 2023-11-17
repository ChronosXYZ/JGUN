package io.github.chronosx88.JGUN.examples;

import io.github.chronosx88.JGUN.api.Gun;
import io.github.chronosx88.JGUN.api.graph.ArrayBuilder;
import io.github.chronosx88.JGUN.api.graph.NodeBuilder;
import io.github.chronosx88.JGUN.models.Result;
import io.github.chronosx88.JGUN.network.NetworkNode;
import io.github.chronosx88.JGUN.storage.MemoryStorage;
import io.github.chronosx88.JGUN.storage.Storage;

import java.net.Inet4Address;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class MainClient {
    public static void main(String[] args) throws URISyntaxException, UnknownHostException, ExecutionException, InterruptedException {
        Storage storage = new MemoryStorage();
        NetworkNode peer = new NetworkNode(Inet4Address.getByAddress(new byte[]{127, 0, 0, 1}), 5054, storage);
        Gun gun = new Gun(storage, peer);
        Result result = gun.get("person").put(new NodeBuilder()
                .add("firstName", "ABCD")
                .build()).get();
        System.out.println(result);
        result = gun.get("person").get("address").put(new NodeBuilder()
                .add("city", "HUY")
                .add("ZIP", new NodeBuilder()
                        .add("post", "pochta rossii"))
                .build()).get();
        System.out.println(result);
    }
}
