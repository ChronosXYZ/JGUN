package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;
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
        new Thread(() -> {
            Iterator<String> iterator = path.iterator();
            String rootSoul = iterator.next();
            String field = iterator.hasNext() ? iterator.next() : null;

            iterator = path.iterator();
            iterator.next();

            CompletableFuture<JSONObject> future = CompletableFuture.supplyAsync(() -> {
                FutureGet futureGetRootSoul = new FutureGet(Dup.random());
                dispatcher.addPendingFuture(futureGetRootSoul);
                dispatcher.sendGetRequest(futureGetRootSoul.getFutureID(), rootSoul, field);
                JSONObject result = futureGetRootSoul.await();
                if(result != null && result.isEmpty()) {
                    result = null;
                }
                return result == null ? null : result.getJSONObject(rootSoul);
            });
            do {
                String soul = iterator.hasNext() ? iterator.next() : null;
                String nextField = iterator.hasNext() ? iterator.next() : null;
                future = future.thenApply(jsonObject -> {
                    if(jsonObject != null) {
                        if(soul != null) {
                            if(jsonObject.get(soul) instanceof JSONObject) {
                                String nodeRef = jsonObject.getJSONObject(soul).getString("#");
                                FutureGet get = new FutureGet(Dup.random());
                                dispatcher.addPendingFuture(get);
                                dispatcher.sendGetRequest(get.getFutureID(), nodeRef, nextField);
                                JSONObject result = get.await();
                                if(result != null && result.isEmpty()) {
                                    result = null;
                                }
                                return result == null ? null : result.getJSONObject(nodeRef);
                            }
                        } else {
                            return jsonObject;
                        }
                    }
                    return null;
                });
            } while(iterator.hasNext());

            try {
                JSONObject data = future.get();
                futureGet.complete(data);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).start();
        return futureGet;
    }

    public FuturePut put(JSONObject data) {
        FuturePut futurePut = new FuturePut(Dup.random());
        dispatcher.addPendingFuture(futurePut);
        JSONObject temp = new JSONObject();
        JSONObject temp1 = temp;
        for (int i = 0; i < path.size(); i++) {
            if(i != path.size() - 1) {
                JSONObject object = new JSONObject();
                temp1.put(path.get(i), object);
                temp1 = object;
            } else {
                temp1.put(path.get(i), data == null ? JSONObject.NULL : data);
            }

        }
        dispatcher.sendPutRequest(futurePut.getFutureID(), temp);
        return futurePut;
    }

    public void on(NodeChangeListener changeListener) {
        dispatcher.addChangeListener(Utils.join("/", path), changeListener);
    }

    public void map(NodeChangeListener.ForEach forEachListener) {
        dispatcher.addForEachChangeListener(Utils.join("/", path), forEachListener);
    }
}
