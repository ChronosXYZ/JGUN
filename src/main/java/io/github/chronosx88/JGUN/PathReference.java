package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.futures.FutureGet;
import io.github.chronosx88.JGUN.futures.FuturePut;

import java.util.ArrayList;
import java.util.HashMap;

public class PathReference {
    private final ArrayList<String> path = new ArrayList<>();

    private Gun database;

    public PathReference(Gun db) {
        this.database = db;
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
        database.addChangeListener(String.join("/", path), changeListener);
    }

    public void map(NodeChangeListener.ForEach forEachListener) {
        database.addForEachChangeListener(String.join("/", path), forEachListener);
    }
}
