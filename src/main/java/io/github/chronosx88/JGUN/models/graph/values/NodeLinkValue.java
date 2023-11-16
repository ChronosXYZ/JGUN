package io.github.chronosx88.JGUN.models.graph.values;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.chronosx88.JGUN.models.graph.NodeValue;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class NodeLinkValue implements NodeValue {
    @JsonProperty("#")
    private String link;

    @Override
    @JsonIgnore
    public ValueType getValueType() {
        return ValueType.LINK;
    }
}
