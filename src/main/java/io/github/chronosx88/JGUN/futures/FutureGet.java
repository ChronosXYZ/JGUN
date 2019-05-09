package io.github.chronosx88.JGUN.futures;

import org.json.JSONObject;

public class FutureGet extends BaseCompletableFuture<JSONObject> {
    public FutureGet(String id) {
        super(id);
    }
}
