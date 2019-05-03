package io.github.chronosx88.GunJava;

import io.github.chronosx88.GunJava.storageBackends.MemoryBackend;
import io.github.chronosx88.GunJava.storageBackends.StorageBackend;
import org.json.JSONObject;

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

    public static MemoryBackend getRequest(JSONObject lex, StorageBackend graph) {
        String soul = lex.getString("#");
        String key = lex.optString(".", null);
        Node node = graph.getNode(soul);
        Object tmp;
        if(node == null) {
            return new MemoryBackend();
        }
        if(key != null) {
            tmp = node.values.opt(key);
            if(tmp == null) {
                return new MemoryBackend();
            }
            Node node1 = new Node(node.toJSONObject());
            node = Utils.newNode(node.soul, new JSONObject());
            node.setMetadata(node1.getMetadata());
            node.values.put(key, tmp);
            JSONObject tmpStates = node1.states;
            node.states.put(key, tmpStates.get(key));
        }
        MemoryBackend ack = new MemoryBackend();
        ack.addNode(soul, node);
        return ack;
    }
}
