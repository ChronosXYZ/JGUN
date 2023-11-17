package io.github.chronosx88.JGUN.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.chronosx88.JGUN.models.NetworkMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Jacksonized
public class GetRequest extends NetworkMessage implements Request {
    @JsonProperty("get")
    private GetRequestParams params;
}
