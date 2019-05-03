package io.github.chronosx88.GunJava;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Dup {
    private static char[] randomPack = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static Random random = new Random(System.currentTimeMillis());
    private Map<String, Long> s = new ConcurrentHashMap<>();
    private DupOpt opt = new DupOpt();
    private Thread to = null;

    public Dup() {
        opt.max = 1000;
        opt.age = 1000 * 9;
    }

    public String track(String id) {
        s.put(id, System.currentTimeMillis());
        if(to == null) {
            Utils.setTimeout(() -> {
                for(Map.Entry<String, Long> entry : s.entrySet()) {
                    if(opt.age > (System.currentTimeMillis() - entry.getValue()))
                        continue;
                    s.remove(entry.getKey());
                }
                to = null;
            }, opt.age);
        }
        return id;
    }

    public boolean check(String id) {
        if(s.containsKey(id)) {
            track(id);
            return true;
        } else {
            return false;
        }
    }

    public static String random(int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(randomPack[random.nextInt(randomPack.length)]);
        }
        return sb.toString();
    }
}
