package io.github.chronosx88.JGUN.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.chronosx88.JGUN.models.BaseMessage;
import io.github.chronosx88.JGUN.models.graph.MemoryGraph;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class PutRequest extends BaseMessage implements Request {
    @JsonProperty("put")
    private MemoryGraph graph;
}
