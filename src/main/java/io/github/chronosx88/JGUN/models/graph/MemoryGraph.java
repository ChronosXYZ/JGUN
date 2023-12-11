package io.github.chronosx88.JGUN.models.graph;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class MemoryGraph {
    @JsonIgnore
    public Map<String, Node> nodes = new LinkedHashMap<>();

    @JsonAnyGetter
    public Map<String, Node> nodes() {
        return nodes;
    }

    @JsonAnySetter
    public void putNodes(String id, Node node) {
        nodes.put(id, node);
    }
}
