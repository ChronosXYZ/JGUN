package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import io.github.chronosx88.JGUN.storageBackends.StorageBackend;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;

public class Utils {
    public static Thread setTimeout(Runnable runnable, int delay){
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });
        thread.start();
        return thread;
    }

    public static Node newNode(String soul, JSONObject data) {
        JSONObject states = new JSONObject();
        for (String key : data.keySet()) {
            states.put(key, System.currentTimeMillis());
        }
        data.put("_", new JSONObject().put("#", soul).put(">", states));
        return new Node(data);
    }

    public static InMemoryGraph getRequest(JSONObject lex, StorageBackend graph) {
        String soul = lex.getString("#");
        String key = lex.optString(".", null);
        Node node = graph.getNode(soul);
        Object tmp;
        if(node == null) {
            return new InMemoryGraph();
        }
        if(key != null) {
            tmp = node.values.opt(key);
            if(tmp == null) {
                return new InMemoryGraph();
            }
            Node node1 = new Node(node.toJSONObject());
            node = Utils.newNode(node.soul, new JSONObject());
            node.setMetadata(node1.getMetadata());
            node.values.put(key, tmp);
            JSONObject tmpStates = node1.states;
            node.states.put(key, tmpStates.get(key));
        }
        InMemoryGraph ack = new InMemoryGraph();
        ack.addNode(soul, node);
        return ack;
    }

    public static InMemoryGraph prepareDataForPut(JSONObject data) {
        InMemoryGraph result = new InMemoryGraph();
        for (String objectKey : data.keySet()) {
            JSONObject object = data.getJSONObject(objectKey);
            Node node = Utils.newNode(objectKey, object);
            ArrayList<String> path = new ArrayList<>();
            path.add(objectKey);
            prepareNodeForPut(node, result, path);
        }
        return result;
    }

    private static void prepareNodeForPut(Node node, InMemoryGraph result, ArrayList<String> path) {
        for(String key : new ConcurrentSkipListSet<>(node.values.keySet())) {
            Object value = node.values.get(key);
            if(value instanceof JSONObject) {
                path.add(key);
                String soul = "";
                soul = String.join("/", path);
                Node tmpNode = Utils.newNode(soul, (JSONObject) value);
                node.values.remove(key);
                node.values.put(key, new JSONObject().put("#", soul));
                prepareNodeForPut(tmpNode, result, new ArrayList<>(path));
                result.addNode(soul, tmpNode);
                path.remove(key);
            }
        }
        result.addNode(node.soul, node);
    }

    public static JSONObject formatGetRequest(String key, String field) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("#", Dup.random());
        JSONObject getParameters = new JSONObject();
        getParameters.put("#", key);
        if(field != null) {
            getParameters.put(".", field);
        }
        jsonObject.put("get", getParameters);
        return jsonObject;
    }

    public static JSONObject formatPutRequest(JSONObject data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("#", Dup.random());
        jsonObject.put("put", data);
        return jsonObject;
    }

    /**
     * This check current nodes for existing IDs in our storage, and if there are existing IDs, it means to replace them.
     * Prevents trailing nodes in storage
     * @param incomingGraph The graph that came to us over the wire.
     * @param graphStorage Graph storage in which the incoming graph will be saved
     * @return Prepared graph for saving
     */
    /*public static InMemoryGraph checkIncomingNodesForID(InMemoryGraph incomingGraph, StorageBackend graphStorage) {
        for (Node node : incomingGraph.nodes()) {
            for(node)
        }
    }*/
}
