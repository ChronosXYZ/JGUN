package io.github.chronosx88.JGUN.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public abstract class BaseMessage {
    @JsonProperty("#")
    private String id;
}
