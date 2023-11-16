package io.github.chronosx88.JGUN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.github.chronosx88.JGUN.api.graph.NodeBuilder;
import io.github.chronosx88.JGUN.api.graph.ArrayBuilder;
import io.github.chronosx88.JGUN.models.graph.MemoryGraph;
import io.github.chronosx88.JGUN.models.graph.values.IntValue;
import io.github.chronosx88.JGUN.models.graph.values.NodeLinkValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphNodeBuilderTest {

    @Test
    void Test_sampleGraph1() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        var graph = new NodeBuilder()
                .add("firstName", "John")
                .add("lastName", "Smith")
                .add("age", 25)
                .add("address", new NodeBuilder()
                        .add("streetAddress", "21 2nd Street")
                        .add("city", "New York")
                        .add("state", "NY")
                        .add("postalCode", "10021"))
                .add("phoneNumber", new ArrayBuilder()
                        .add(new NodeBuilder()
                                .add("type", "home")
                                .add("number", "212 555-1234"))
                        .add(new NodeBuilder()
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .addNull("heh")
                .build();

        String graphJSON1 = objectMapper.writeValueAsString(graph);
        System.out.println(graphJSON1);

        graph = objectMapper.readValue(graphJSON1, MemoryGraph.class);
        String graphJSON2 = objectMapper.writeValueAsString(graph);
        System.out.println(graphJSON2);

        assertEquals(graphJSON1, graphJSON2);
        assertEquals(((IntValue) graph.nodes.get("__ROOT__").getValues().get("age")).getInt(), 25);
        assertTrue(graph.nodes.get("__ROOT__").getValues().get("address") instanceof NodeLinkValue);
    }

    @Test
    void Test_sampleGraph2() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        var graph = new NodeBuilder()
                .add("a", new ArrayBuilder()
                        .add(new NodeBuilder()
                                .add("b", new NodeBuilder()
                                        .add("c", true)))
                        .add(0))
                .build();

        String graphJSON = objectMapper.writeValueAsString(graph);
        System.out.println(graphJSON);

        graph = objectMapper.readValue(graphJSON, MemoryGraph.class);
        graphJSON = objectMapper.writeValueAsString(graph);
        System.out.println(graphJSON);
    }
}