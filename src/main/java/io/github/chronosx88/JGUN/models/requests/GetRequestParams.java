package io.github.chronosx88.JGUN.models.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetRequestParams {
    @JsonProperty("#")
    private String nodeID;

    @JsonProperty(".")
    private String field;
}
