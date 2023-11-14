package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.models.Node;

@FunctionalInterface
public interface NodeChangeListener {
    void onChange(Node node);

    interface Map {
        void onChange(String key, Object value);
    }
}
