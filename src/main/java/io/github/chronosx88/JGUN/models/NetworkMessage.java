package io.github.chronosx88.JGUN.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@JsonDeserialize(using = NetworkMessageDeserializer.class)
@SuperBuilder
public abstract class NetworkMessage {
    @JsonProperty("#")
    private String id;
}
