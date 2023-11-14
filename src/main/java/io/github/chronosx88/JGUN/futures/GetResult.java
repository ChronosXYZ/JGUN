package io.github.chronosx88.JGUN.futures;

import io.github.chronosx88.JGUN.models.MemoryGraph;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class GetResult extends Result {
    private final MemoryGraph data;
}
