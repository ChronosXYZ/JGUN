package io.github.chronosx88.JGUN.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@Jacksonized
public class MemoryGraph {
    @JsonIgnore
    @Builder.Default
    public final Map<String, Node> nodes = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Node> nodes() {
        return nodes;
    }

    @JsonAnySetter
    public void putNodes(String id, Node node) {
        nodes.put(id, node);
    }
}
