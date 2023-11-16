package io.github.chronosx88.JGUN.models.graph.values;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.github.chronosx88.JGUN.models.graph.NodeValue;
import lombok.Getter;

@Getter
public class StringValue implements NodeValue {
    @JsonValue
    private String value;

    @JsonCreator
    public StringValue(String value) {
        this.value = value;
    }

    @Override
    public ValueType getValueType() {
        return ValueType.STRING;
    }

}
