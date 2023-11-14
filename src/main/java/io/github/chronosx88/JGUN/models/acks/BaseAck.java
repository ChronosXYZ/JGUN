package io.github.chronosx88.JGUN.models.acks;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.chronosx88.JGUN.models.BaseMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Jacksonized
public class BaseAck extends BaseMessage {
    @JsonProperty("@")
    private String replyTo;
    private boolean ok;
}