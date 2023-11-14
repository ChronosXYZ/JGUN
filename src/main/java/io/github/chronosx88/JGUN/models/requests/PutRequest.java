package io.github.chronosx88.JGUN.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.chronosx88.JGUN.models.BaseMessage;
import io.github.chronosx88.JGUN.models.MemoryGraph;
import io.github.chronosx88.JGUN.models.Node;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
@EqualsAndHashCode(callSuper = true)
public class PutRequest extends BaseMessage {
    @JsonProperty("put")
    private MemoryGraph graph;
}
