package io.github.chronosx88.JGUN.api;

import io.github.chronosx88.JGUN.models.graph.Node;

@FunctionalInterface
public interface NodeChangeListener {
    void onChange(Node node);

    interface Map {
        void onChange(String key, Object value);
    }
}
