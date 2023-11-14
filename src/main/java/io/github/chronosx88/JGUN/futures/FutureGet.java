package io.github.chronosx88.JGUN.futures;

import io.github.chronosx88.JGUN.models.MemoryGraph;

public class FutureGet extends BaseCompletableFuture<MemoryGraph> {
    public FutureGet(String id) {
        super(id);
    }
}
