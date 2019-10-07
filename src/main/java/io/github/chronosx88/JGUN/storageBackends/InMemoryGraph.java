package io.github.chronosx88.JGUN.storageBackends;

import io.github.chronosx88.JGUN.Node;

import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryGraph implements StorageBackend {

    private final HashMap<String, Node> nodes;

    public InMemoryGraph(JSONObject source) {
        nodes = new LinkedHashMap<>();

        for (String soul : source.keySet())
            nodes.put(soul, new Node(source.getJSONObject(soul)));
    }

    public InMemoryGraph() {
        nodes = new LinkedHashMap<>();
    }

    public Node getNode(String soul) {
        return nodes.get(soul);
    }

    public void addNode(String soul, Node incomingNode) {
        nodes.put(soul, incomingNode);
    }

    public boolean hasNode(String soul) {
        return nodes.containsKey(soul);
    }

    public Set<Map.Entry<String, Node>> entries() {
        return nodes.entrySet();
    }

    public Collection<Node> nodes() { return nodes.values(); }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, Node> entry : nodes.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue().toJSONObject());
        }
        return jsonObject.toString();
    }

    public String toPrettyString() {
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, Node> entry : nodes.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue().toJSONObject());
        }
        return jsonObject.toString(2);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, Node> entry : nodes.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue().toJSONObject());
        }
        return jsonObject;
    }

    public JSONObject toUserJSONObject() {
        JSONObject jsonObject = new JSONObject();
        for(Map.Entry<String, Node> entry : nodes.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue().toUserJSONObject());
        }
        return jsonObject;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}
