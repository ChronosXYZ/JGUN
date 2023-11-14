package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;
import io.github.chronosx88.JGUN.nodes.GunClient;

import java.util.ArrayList;
import java.util.HashMap;

public class PathReference {
    private final ArrayList<String> path = new ArrayList<>();

    private Gun gun;

    public PathReference(Gun gun) {
        this.gun = gun;
    }

    public PathReference get(String key) {
        path.add(key);
        return this;
    }

    public FutureGet getData() {
        // TODO
        return null;
    }

    public FuturePut put(HashMap<String, Object> data) {
        // TODO
        return null;
    }

    public void on(NodeChangeListener changeListener) {
        gun.addChangeListener(String.join("/", path), changeListener);
    }

    public void map(NodeChangeListener.Map forEachListener) {
        gun.addMapChangeListener(String.join("/", path), forEachListener);
    }
}
