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
                .add("firstName", "John")
                .add("lastName", "Smith")
                .add("age", 25)
                .add("address", new NodeBuilder()
                        .add("streetAddress", "21 2nd Street")
                        .add("city", "New York")
                        .add("state", "NY")
                        .add("postalCode", "10021"))
                .add("phoneNumber", new ArrayBuilder()
                        .add(new NodeBuilder()
                                .add("type", "home")
                                .add("number", "212 555-1234"))
                        .add(new NodeBuilder()
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .build()).get();
        System.out.println(result);
    }
}
