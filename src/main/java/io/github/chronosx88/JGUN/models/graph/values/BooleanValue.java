package io.github.chronosx88.JGUN.models.graph.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.chronosx88.JGUN.models.graph.NodeValue;
import lombok.Getter;

@Getter
public class BooleanValue implements NodeValue {
    @JsonValue
    private boolean value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public NodeValue.ValueType getValueType() {
        return NodeValue.ValueType.BOOLEAN;
    }
}
