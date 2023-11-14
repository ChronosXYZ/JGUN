package io.github.chronosx88.JGUN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.chronosx88.JGUN.futures.FuturePut;
import io.github.chronosx88.JGUN.models.MemoryGraph;
import io.github.chronosx88.JGUN.models.requests.PutRequest;
import io.github.chronosx88.JGUN.nodes.GunClient;
import io.github.chronosx88.JGUN.storage.Storage;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Gun {
    private GunClient peer;
    private final Storage storage;
    private final ObjectMapper objectMapper;
    private final Executor executorService = Executors.newCachedThreadPool();

    public Gun(InetAddress address, int port, Storage storage) {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        this.storage = storage;
        try {
            this.peer = new GunClient(address, port, storage);
            this.peer.connectBlocking();
        } catch (URISyntaxException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public PathReference get(String key) {
        PathReference pathRef = new PathReference(this);
        pathRef.get(key);
        return pathRef;
    }

    protected void addChangeListener(String nodeID, NodeChangeListener listener) {
        storage.addChangeListener(nodeID, listener);
    }

    protected void addMapChangeListener(String nodeID, NodeChangeListener.Map listener) {
        storage.addMapChangeListener(nodeID, listener);
    }

    protected FuturePut sendPutRequest(MemoryGraph data) {
        String reqID = Dup.random();
        executorService.execute(() -> {
            storage.mergeUpdate(data);
            var request = PutRequest.builder()
                    .id(reqID)
                    .graph(data)
                    .build();
            String encodedRequest;
            try {
                encodedRequest = this.objectMapper.writeValueAsString(request);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            peer.emit(encodedRequest);
        });
        return new FuturePut(reqID);
    }

    protected void sendGetRequest(String key, String field) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }
}
