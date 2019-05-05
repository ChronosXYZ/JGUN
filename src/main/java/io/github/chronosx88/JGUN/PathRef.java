package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.builders.GetBuilder;
import io.github.chronosx88.JGUN.futures.builders.PutBuilder;
import org.json.JSONObject;

import java.util.ArrayList;

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

    public GetBuilder getData() {
        return new GetBuilder(path);
    }

    public PutBuilder put(JSONObject data) {
        return new PutBuilder(dispatcher, data, path);
    }
}
