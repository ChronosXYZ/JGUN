package io.github.chronosx88.GunJava;

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
}
