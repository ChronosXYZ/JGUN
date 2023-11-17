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
@JsonDeserialize(using = NetworkMessageDeserializer.class)
@SuperBuilder
public abstract class NetworkMessage {
    @JsonProperty("#")
    private String id;
}
