package io.github.chronosx88.JGUN.models.graph;

import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DeferredNode extends Node implements Delayed {
    private long deferredUntil = 0;

    DeferredNode(NodeMetadata metadata, Map<String, NodeValue> values) {
        super(metadata, values);
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        long delay = deferredUntil - System.currentTimeMillis();
        return timeUnit.convert(delay, TimeUnit.MILLISECONDS);
    }

    public void setDelay(long delayDuration) {
        this.deferredUntil = System.currentTimeMillis() + delayDuration;
    }

    @Override
    public int compareTo(Delayed delayed) {
        return Math.toIntExact(this.deferredUntil - ((DeferredNode) delayed).deferredUntil);
    }

}
