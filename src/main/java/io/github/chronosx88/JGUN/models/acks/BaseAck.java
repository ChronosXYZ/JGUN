package io.github.chronosx88.JGUN.models.acks;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.chronosx88.JGUN.models.BaseMessage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseAck extends BaseMessage {
    @JsonProperty("@")
    private String replyTo;
    private String ok;
}
