package io.github.chronosx88.JGUN.network;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Dup {
    private final Cache<String, Long> cache;

    public Dup(long age) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(age, TimeUnit.SECONDS)
                .build();
    }

    public void track(String id) {
        cache.put(id, System.currentTimeMillis());
    }

    public boolean checkDuplicated(String id) {
        Long timestamp = null;
        try {
            timestamp = cache.getIfPresent(id);
        } catch (NullPointerException ignored) {}
        if(Objects.nonNull(timestamp)) {
            return true;
        } else {
            track(id);
            return false;
        }
    }

    public static String random() {
        return UUID.randomUUID().toString();
    }
}
