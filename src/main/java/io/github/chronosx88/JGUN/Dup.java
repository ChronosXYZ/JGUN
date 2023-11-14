package io.github.chronosx88.JGUN;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Dup {
    private final Cache<String, Long> cache;

    public Dup(long age) {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration<String, Long> config = new MutableConfiguration<>();
        config.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, age)));
        this.cache = cacheManager.createCache("dup", config);
    }

    private void track(String id) {
        cache.put(id, System.currentTimeMillis());
    }

    public boolean isDuplicated(String id) {
        if(cache.containsKey(id)) {
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
