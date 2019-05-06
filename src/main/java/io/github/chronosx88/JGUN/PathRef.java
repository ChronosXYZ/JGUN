package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.builders.PutBuilder;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class PathRef {
    private final ArrayList<String> path = new ArrayList<>();
    private Dispatcher dispatcher;

    public PathRef(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public PathRef get(String key) {
        path.add(key);
        return this;
    }

    public FutureGet getData() {
        FutureGet futureGet = new FutureGet(Dup.random());
        Iterator<String> iterator = path.iterator();
        new Thread(() -> {
            CompletableFuture<JSONObject> future = CompletableFuture.supplyAsync(() -> {
                String rootSoul = iterator.next();
                String field = iterator.hasNext() ? iterator.next() : null;
                FutureGet futureGetRootSoul = new FutureGet(Dup.random());
                dispatcher.addPendingFuture(futureGetRootSoul);
                dispatcher.sendGetRequest(rootSoul, field);
                futureGetRootSoul.awaitUninterruptibly();
                if(futureGetRootSoul.isSuccess() && futureGetRootSoul.getData() != null) {
                    return futureGetRootSoul.getData();
                } else {
                    return null;
                }
            });
            while(iterator.hasNext()) {
                future = future.thenApply(jsonObject -> {
                    String soul = iterator.next();
                    String field = iterator.hasNext() ? iterator.next() : null;
                    if(jsonObject != null) {
                        String nodeRef = jsonObject.getJSONObject(soul).getString("#");
                        FutureGet get = new FutureGet(Dup.random());
                        dispatcher.addPendingFuture(get);
                        dispatcher.sendGetRequest(nodeRef, field);
                        get.awaitUninterruptibly();
                        if(get.isSuccess() && get.getData() != null) {
                            return get.getData();
                        } else {
                            return null;
                        }
                    }
                    return null;
                });
            }
            try {
                JSONObject data = future.get();
                futureGet.done(data);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
        return futureGet;
    }

    public PutBuilder put(JSONObject data) {
        return new PutBuilder(dispatcher, data, path);
    }
}
