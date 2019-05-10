package io.github.chronosx88.JGUN.futures;

import java9.util.concurrent.CompletableFuture;

import java.util.concurrent.ExecutionException;


public class BaseCompletableFuture<T> extends CompletableFuture<T> {
    private final String futureID;

    public BaseCompletableFuture(String id) {
        super();
        futureID = id;
    }

    public String getFutureID() {
        return futureID;
    }

    public void addListener(final BaseFutureListener<T> listener) {
        this.whenCompleteAsync((t, throwable) -> {
            if(throwable == null) {
                listener.onComplete(t);
            } else {
                listener.onError(t, throwable);
            }
        });
    }

    public T await() {
        T t = null;
        try {
            t = super.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return t;
    }
}
