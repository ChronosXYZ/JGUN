package io.github.chronosx88.JGUN.models.acks;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.chronosx88.JGUN.models.BaseMessage;
import io.github.chronosx88.JGUN.models.graph.MemoryGraph;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Jacksonized
public class GetAck extends BaseMessage {
    @JsonProperty("put")
    private MemoryGraph data;
    @JsonProperty("@")
    private String replyTo;
    private boolean ok;
}
