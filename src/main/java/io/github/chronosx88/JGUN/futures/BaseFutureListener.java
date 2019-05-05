package io.github.chronosx88.JGUN.futures;

public interface BaseFutureListener<F extends BaseFuture> {
    void onComplete(F future);
    void onError(F future, Throwable exception);
}
