package io.github.chronosx88.JGUN.api;

import io.github.chronosx88.JGUN.models.Result;

/**
 * Return success of PUT operation
 */
public class FuturePut extends BaseCompletableFuture<Result> {
    public FuturePut(String id) {
        super(id);
    }
}
