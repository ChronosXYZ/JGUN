package io.github.chronosx88.JGUN.models;

import io.github.chronosx88.JGUN.models.graph.Node;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class GetResult extends Result {
    private final Node data;
}
