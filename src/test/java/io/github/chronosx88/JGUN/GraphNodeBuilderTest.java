package io.github.chronosx88.JGUN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.jupiter.api.Test;

class GraphNodeBuilderTest {

    @Test
    void Test_sampleGraph1() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        var graph = new GraphNodeBuilder()
                .add("firstName", "John")
                .add("lastName", "Smith")
                .add("age", 25)
                .add("address", new GraphNodeBuilder()
                        .add("streetAddress", "21 2nd Street")
                        .add("city", "New York")
                        .add("state", "NY")
                        .add("postalCode", "10021"))
                .add("phoneNumber", new NodeArrayBuilder()
                        .add(new GraphNodeBuilder()
                                .add("type", "home")
                                .add("number", "212 555-1234"))
                        .add(new GraphNodeBuilder()
                                .add("type", "fax")
                                .add("number", "646 555-4567")))
                .build();

        String graphJSON = null;
        try {
            graphJSON = objectMapper.writeValueAsString(graph);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(graphJSON);
    }

    @Test
    void Test_sampleGraph2() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        var graph = new GraphNodeBuilder()
                .add("a", new NodeArrayBuilder()
                        .add(new GraphNodeBuilder()
                                .add("b", new GraphNodeBuilder()
                                        .add("c", true)))
                        .add(0))
                .build();

        String graphJSON = null;
        try {
            graphJSON = objectMapper.writeValueAsString(graph);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(graphJSON);
    }
}