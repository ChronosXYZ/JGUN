package io.github.chronosx88.JGUN.futures;

import java.util.concurrent.ExecutionException;

import java.util.concurrent.CompletableFuture;
import lombok.Getter;


@Getter
public class BaseCompletableFuture<T> extends CompletableFuture<T> {
    private final String futureID;

    public BaseCompletableFuture(String id) {
        super();
        futureID = id;
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
