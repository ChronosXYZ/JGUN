package io.github.chronosx88.GunJava.storageBackends;

import io.github.chronosx88.GunJava.Node;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface StorageBackend {
    Node getNode(String soul);
    void addNode(String soul, Node node);
    boolean hasNode(String soul);
    Set<Map.Entry<String, Node>> entries();
    Collection<Node> nodes();
    String toString();
    String toPrettyString();
    JSONObject toJSONObject();
    boolean isEmpty();
}
