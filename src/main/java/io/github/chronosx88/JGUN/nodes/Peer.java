package io.github.chronosx88.JGUN.nodes;

import io.github.chronosx88.JGUN.futures.BaseCompletableFuture;
import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;

public interface Peer {
    void emit(String data);
    void addPendingPutRequest(FuturePut futurePut);
    void addPendingGetRequest(FutureGet futureGet);
    void start();
}
