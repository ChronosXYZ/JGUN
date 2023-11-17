package io.github.chronosx88.JGUN.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.chronosx88.JGUN.api.Gun;
import io.github.chronosx88.JGUN.api.NodeChangeListener;
import io.github.chronosx88.JGUN.api.graph.ArrayBuilder;
import io.github.chronosx88.JGUN.api.graph.NodeBuilder;
import io.github.chronosx88.JGUN.models.Result;
import io.github.chronosx88.JGUN.models.graph.Node;
import io.github.chronosx88.JGUN.network.GatewayNetworkNode;
import io.github.chronosx88.JGUN.network.NetworkNode;
import io.github.chronosx88.JGUN.storage.MemoryStorage;
import io.github.chronosx88.JGUN.storage.Storage;

import java.net.Inet4Address;
import java.util.concurrent.ExecutionException;

public class MainServer {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Storage storage = new MemoryStorage();
        GatewayNetworkNode peer = new GatewayNetworkNode(5054, storage);
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
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        gun.get("person").on(node -> {
            try {
                System.out.println(mapper.writeValueAsString(node));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        gun.get("person").get("address").on(node -> {
            try {
                System.out.println(mapper.writeValueAsString(node));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(result.isOk());
    }
}
