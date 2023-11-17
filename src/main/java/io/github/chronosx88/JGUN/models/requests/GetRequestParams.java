package io.github.chronosx88.JGUN.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class GetRequestParams {
    @JsonProperty("#")
    private String nodeId;

    @JsonProperty(".")
    private String field;
}
