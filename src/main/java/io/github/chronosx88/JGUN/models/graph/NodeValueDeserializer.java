package io.github.chronosx88.JGUN.models.graph;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.chronosx88.JGUN.models.graph.values.*;

import java.io.IOException;
import java.util.ArrayList;

public class NodeValueDeserializer extends JsonDeserializer<NodeValue> {
    @Override
    public NodeValue deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.readValueAsTree();
        return parseValue(node);
    }

    private NodeValue parseValue(JsonNode node) throws JsonProcessingException {
        if (node.isBoolean()) {
            return new BooleanValue(node.booleanValue());
        } else if (node.isBigDecimal()) {
            return new DecimalValue(node.decimalValue());
        } else if (node.isFloat()) {
            return new DecimalValue(node.floatValue());
        } else if (node.isDouble()) {
            return new DecimalValue(node.doubleValue());
        } else if (node.isTextual()) {
            return new StringValue(node.textValue());
        } else if (node.isInt()) {
            return new IntValue(node.intValue());
        } else if (node.isBigInteger()) {
            return new IntValue(node.bigIntegerValue());
        } else if (node.isLong()) {
            return new IntValue(node.longValue());
        } else if (node.isObject()) {
            if (node.has("#")) {
                return NodeLinkValue.builder()
                        .link(node.get("#").textValue())
                        .build();
            } else {
                throw new IllegalArgumentException("node can have only links, not actual objects");
            }
        } else if (node.isArray()) {
            ArrayValue value = new ArrayValue();
            for (JsonNode arrayItem : node) {
                value.getValue().add(parseValue(arrayItem));
            }
            return value;
        }
        throw new IllegalArgumentException("unsupported node value");
    }
}
