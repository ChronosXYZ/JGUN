package io.github.chronosx88.JGUN.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.chronosx88.JGUN.api.FutureGet;
import io.github.chronosx88.JGUN.api.FuturePut;
import io.github.chronosx88.JGUN.models.GetResult;
import io.github.chronosx88.JGUN.models.Result;
import io.github.chronosx88.JGUN.models.graph.MemoryGraph;
import io.github.chronosx88.JGUN.models.requests.GetRequest;
import io.github.chronosx88.JGUN.models.requests.GetRequestParams;
import io.github.chronosx88.JGUN.models.requests.PutRequest;
import io.github.chronosx88.JGUN.models.requests.Request;
import lombok.Getter;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkManager {
    private final ObjectMapper objectMapper;

    private final Peer peer;

    private final Executor executorService = Executors.newCachedThreadPool();

    /**
     * Default network timeout (in seconds)
     */
    @Getter
    private final int timeout;

    public NetworkManager(Peer peer) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        this.peer = peer;
        this.timeout = peer.getTimeout();
    }

    public void start() {
        executorService.execute(this.peer::start);
    }

    private <T extends Request> void sendRequest(T request) {
        String encodedRequest;
        try {
            encodedRequest = this.objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        peer.emit(encodedRequest);
    }

    public FuturePut sendPutRequest(MemoryGraph putData) {
        String id = Dup.random();
        var requestFuture = new FuturePut(id);
        if (this.peer.connectedPeerCount() > 0) {
            executorService.execute(() -> this.sendRequest(PutRequest.builder()
                    .id(id)
                    .graph(putData)
                    .build()));
            peer.addPendingPutRequest(requestFuture);
        }
        requestFuture.complete(Result.builder().ok(true).build());
        return requestFuture;
    }

    public FutureGet sendGetRequest(GetRequestParams params) {
        String id = Dup.random();
        var requestFuture = new FutureGet(id, params);
        if (this.peer.connectedPeerCount() > 0) {
            executorService.execute(() -> this.sendRequest(GetRequest.builder()
                    .id(id)
                    .params(params)
                    .build()));
            peer.addPendingGetRequest(requestFuture);
        } else {
            requestFuture.complete(GetResult.builder().data(null).build());
        }
        return requestFuture;
    }
}
