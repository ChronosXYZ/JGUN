package io.github.chronosx88.JGUN.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.chronosx88.JGUN.models.BaseMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetRequest extends BaseMessage {
    @JsonProperty("get")
    private GetRequestParams params;
}
