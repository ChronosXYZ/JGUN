package io.github.chronosx88.JGUN.models.graph;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

//@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@JsonDeserialize(using = NodeValueDeserializer.class)
public interface NodeValue {
    enum ValueType {
        ARRAY,
        STRING,
        INTEGER,
        DECIMAL,
        BOOLEAN,
        NULL,
        LINK
    }

    ValueType getValueType();
}