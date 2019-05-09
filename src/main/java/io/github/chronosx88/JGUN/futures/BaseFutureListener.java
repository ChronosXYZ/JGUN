package io.github.chronosx88.JGUN.futures;

public interface BaseFutureListener<T> {
    void onComplete(T result);
    void onError(T result, Throwable exception);
}
