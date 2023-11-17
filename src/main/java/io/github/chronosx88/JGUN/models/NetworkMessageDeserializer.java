package io.github.chronosx88.JGUN.models;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.chronosx88.JGUN.models.acks.Ack;
import io.github.chronosx88.JGUN.models.acks.GetAck;
import io.github.chronosx88.JGUN.models.requests.GetRequest;
import io.github.chronosx88.JGUN.models.requests.PutRequest;

import java.io.IOException;

public class NetworkMessageDeserializer extends JsonDeserializer<NetworkMessage> {
    @Override
    public NetworkMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.readValueAsTree();

        if (node.has("#")) {
            // parsing ack
            if (node.has("@")) {
                if (node.has("put")) {
                    return p.getCodec().treeToValue(node, GetAck.class);
                }
                return p.getCodec().treeToValue(node, Ack.class);
            }

            if (node.has("get")) {
                return p.getCodec().treeToValue(node, GetRequest.class);
            } else if (node.has("put")) {
                return p.getCodec().treeToValue(node, PutRequest.class);
            }
        }
        throw new IllegalArgumentException("invalid message received");
    }
}
