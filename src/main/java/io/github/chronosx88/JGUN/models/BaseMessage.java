package io.github.chronosx88.JGUN.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.chronosx88.JGUN.models.acks.Ack;
import io.github.chronosx88.JGUN.models.acks.GetAck;
import io.github.chronosx88.JGUN.models.requests.GetRequest;
import io.github.chronosx88.JGUN.models.requests.PutRequest;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(GetRequest.class),
        @JsonSubTypes.Type(PutRequest.class),
        @JsonSubTypes.Type(Ack.class),
        @JsonSubTypes.Type(GetAck.class)
})
@SuperBuilder
public abstract class BaseMessage {
    @JsonProperty("#")
    private String id;
}
