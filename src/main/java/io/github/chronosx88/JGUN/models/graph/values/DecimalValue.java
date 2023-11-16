package io.github.chronosx88.JGUN.models.graph.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.chronosx88.JGUN.models.graph.NodeValue;

import java.math.BigDecimal;

public class DecimalValue implements NodeValue {
    @JsonValue
    private BigDecimal value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public DecimalValue(double num) {
        this.value = BigDecimal.valueOf(num);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public DecimalValue(float num) {
        this.value = BigDecimal.valueOf(num);
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public DecimalValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.DECIMAL;
    }

    public double getDouble() {
        return value.floatValue();
    }

    public float getFloat() {
        return value.floatValue();
    }
}
