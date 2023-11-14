package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;
import io.github.chronosx88.JGUN.models.MemoryGraph;

import java.util.ArrayList;
import java.util.List;

public class PathReference {
    private final List<String> path = new ArrayList<>();

    private final Gun gun;

    public PathReference(Gun gun) {
        this.gun = gun;
    }

    public PathReference get(String key) {
        path.add(key);
        return this;
    }

    public FutureGet once() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public FuturePut put(MemoryGraph graph) {
        return gun.sendPutRequest(graph);
    }

    public void on(NodeChangeListener changeListener) {
        gun.addChangeListener(String.join("/", path), changeListener);
    }

    public void map(NodeChangeListener.Map forEachListener) {
        gun.addMapChangeListener(String.join("/", path), forEachListener);
    }
}
