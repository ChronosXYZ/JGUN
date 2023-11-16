package io.github.chronosx88.JGUN.models.graph;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@Jacksonized
public class NodeMetadata {
    @JsonProperty(">")
    @Builder.Default
    private Map<String, Long> states = new LinkedHashMap<>(); // field -> state

    @JsonProperty("#")
    private String nodeID;
}
