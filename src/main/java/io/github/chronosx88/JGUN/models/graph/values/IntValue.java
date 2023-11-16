package io.github.chronosx88.JGUN.models.graph.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.chronosx88.JGUN.models.graph.NodeValue;

import java.math.BigInteger;

public class IntValue implements NodeValue {
    @JsonValue
    public BigInteger value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public IntValue(int value) {
        this.value = BigInteger.valueOf(value);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public IntValue(long value) {
        this.value = BigInteger.valueOf(value);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public IntValue(BigInteger value) {
        this.value = value;
    }

    public int getInt() {
        return value.intValue();
    }

    public BigInteger getBig() {
        return value;
    }

    public long getLong() {
        return value.longValue();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.INTEGER;
    }
}
