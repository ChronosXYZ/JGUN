package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import org.json.JSONObject;

public class Node implements Comparable<Node> {
    public JSONObject values; // Data
    public JSONObject states; // Metadata for diff
    public String soul; // i.e. ID of node

    /**
     * Create a Peer from a JSON object.
     *
     * @param rawData JSON object, which contains the data
     */
    public Node(JSONObject rawData) {
        this.values = new JSONObject(rawData.toString());
        this.states = values.getJSONObject("_").getJSONObject(">");
        this.soul = values.getJSONObject("_").getString("#");
        values.remove("_");
    }

    @Override
    public int compareTo(Node other) {
        return soul.compareTo(other.soul);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (other instanceof String)
            return soul.equals(other);
        if (other instanceof Node)
            return compareTo((Node) other) == 0;
        return false;
    }

    @Override
    public int hashCode() {
        return soul.hashCode();
    }

    public boolean isNode(String key) {
        return values.optJSONObject(key) != null;
    }

    public Node getNode(String key, InMemoryGraph g) {
        String soulRef = values.getJSONObject(key).getString("#");
        return g.getNode(soulRef);
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject(values.toString());
        jsonObject.put("_", new JSONObject().put("#", soul).put(">", states));
        return jsonObject.toString();
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject(values.toString());
        jsonObject.put("_", new JSONObject().put("#", soul).put(">", states));
        return jsonObject;
    }

    public JSONObject getMetadata() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_", new JSONObject().put("#", soul).put(">", states));
        return jsonObject;
    }

    public void setMetadata(JSONObject metadata) {
        soul = metadata.getJSONObject("_").getString("#");
        states = metadata.getJSONObject("_").getJSONObject(">");
    }
}