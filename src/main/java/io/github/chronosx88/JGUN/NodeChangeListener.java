package io.github.chronosx88.JGUN;

import org.json.JSONObject;

@FunctionalInterface
public interface NodeChangeListener {
    void onChange(JSONObject node);
}
