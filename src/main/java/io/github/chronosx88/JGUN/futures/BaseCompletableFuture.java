package io.github.chronosx88.JGUN.futures;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;

@Getter
public class BaseCompletableFuture<T> extends CompletableFuture<T> {
    private final String futureID;

    public BaseCompletableFuture(String id) {
        super();
        futureID = id;
    }
}
