package io.github.chronosx88.JGUN.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Jacksonized
@Builder
public class Node {
    @JsonProperty("_")
    private NodeMetadata metadata;

    @JsonIgnore
    @Builder.Default
    public Map<String, Object> values = new LinkedHashMap<>(); // Data

    @JsonAnyGetter
    public Map<String, Object> getValues() {
        return values;
    }

    @JsonAnySetter
    public void allSetter(String key, String value) {
        values.put(key, value);
    }
}