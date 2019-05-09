package io.github.chronosx88.JGUN.futures;

/**
 * Return success of PUT operation
 */
public class FuturePut extends BaseCompletableFuture<Boolean> {
    public FuturePut(String id) {
        super(id);
    }
}
