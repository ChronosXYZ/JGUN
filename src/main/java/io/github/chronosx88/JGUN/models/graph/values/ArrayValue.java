package io.github.chronosx88.JGUN.models.graph.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.chronosx88.JGUN.models.graph.NodeValue;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ArrayValue implements NodeValue {
    @JsonValue
    private List<NodeValue> value = new ArrayList<>();

    @JsonCreator
    public ArrayValue(NodeValue[] value) {
        this.value = new ArrayList<>(List.of(value));
    }

    public ArrayValue(List<NodeValue> value) {
        this.value = value;
    }

    public ArrayValue() {
        this.value = new ArrayList<>();
    }

    @Override
    public ValueType getValueType() {
        return ValueType.ARRAY;
    }
}
