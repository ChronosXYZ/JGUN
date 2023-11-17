package io.github.chronosx88.JGUN.network;

import io.github.chronosx88.JGUN.api.FutureGet;
import io.github.chronosx88.JGUN.api.FuturePut;

public interface Peer {
    void emit(String data);
    void addPendingPutRequest(FuturePut futurePut);
    void addPendingGetRequest(FutureGet futureGet);
    void start() throws InterruptedException;
    int getTimeout();
    int connectedPeerCount();
    NetworkHandler getNetworkHandler();
}
