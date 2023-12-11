package io.github.chronosx88.JGUN.api;

import io.github.chronosx88.JGUN.models.graph.Node;
import io.github.chronosx88.JGUN.models.graph.NodeValue;

@FunctionalInterface
public interface NodeChangeListener {
    void onChange(Node node);

    interface Map {
        void onChange(String key, NodeValue value);
    }
}
