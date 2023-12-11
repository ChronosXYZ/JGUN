package io.github.chronosx88.JGUN.models.graph;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Node {
    @JsonProperty("_")
    @Builder.Default
    private final NodeMetadata metadata = new NodeMetadata(new HashMap<>(), null);

    @JsonIgnore
    @Builder.Default
    public final Map<String, NodeValue> values = new LinkedHashMap<>(); // Data

    @JsonAnyGetter
    public Map<String, NodeValue> getValues() {
        return values;
    }

    @JsonAnySetter
    public void allSetter(String key, NodeValue value) {
        values.put(key, value);
    }

    @Override
    public Node clone() {
        return Node.builder()
                .metadata(NodeMetadata.builder()
                        .nodeID(new String(this.getMetadata().getNodeID()))
                        .states(new LinkedHashMap<>(this.getMetadata().getStates())).build())
                .values(new LinkedHashMap<>(this.getValues()))
                .build();
    }
}