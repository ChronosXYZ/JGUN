package io.github.chronosx88.JGUN;

import io.github.chronosx88.JGUN.storageBackends.InMemoryGraph;
import io.github.chronosx88.JGUN.storageBackends.StorageBackend;
import org.json.JSONObject;

import java.util.Map;

public class HAM {
    static class HAMResult {
        public boolean defer = false; // Defer means that the current state is greater than our computer time, and should only be processed when our computer time reaches this state
        public boolean historical = false; // Historical means the old state. This is usually ignored.
        public boolean converge = false; // Everything is fine, you can do merge
        public boolean incoming = false; // Leave incoming value
        public boolean current = false; // Leave current value
        public boolean state = false;
    }

    public static HAMResult ham(long machineState, long incomingState, long currentState, Object incomingValue, Object currentValue) {
        HAMResult result = new HAMResult();

        if(machineState < incomingState) {
            // the incoming value is outside the boundary of the machine's state, it must be reprocessed in another state.
            result.defer = true;
            return result;
        }
        if(incomingState < currentState) {
            // the incoming value is within the boundary of the machine's state, but not within the range.
            result.historical = true;
            return result;
        }
        if(currentState < incomingState) {
            // the incoming value is within both the boundary and the range of the machine's state.
            result.converge = true;
            result.incoming = true;
            return result;
        }
        if(incomingState == currentState) {
            // if incoming state and current state is the same
            if(incomingValue.equals(currentValue)) {
                result.state = true;
                return result;
            }
            if((incomingValue.toString().compareTo(currentValue.toString())) < 0) {
                result.converge = true;
                result.current = true;
                return result;
            }
            if((currentValue.toString().compareTo(incomingValue.toString())) < 0) {
                result.converge = true;
                result.incoming = true;
                return result;
            }
        }
        throw new IllegalArgumentException("Invalid CRDT Data: "+ incomingValue +" to "+ currentValue +" at "+ incomingState +" to "+ currentState +"!");
    }

    public static InMemoryGraph mix(InMemoryGraph change, StorageBackend data) {
        long machine = System.currentTimeMillis();
        InMemoryGraph diff = null;
        for(Map.Entry<String, Node> entry : change.entries()) {
            Node node = entry.getValue();
            for(String key : node.values.keySet()) {
                Object value = node.values.get(key);
                if ("_".equals(key)) { continue; }
                long state = node.states.getLong(key);
                long was = -1;
                Object known = null;
                if(data == null) {
                    data = new InMemoryGraph();
                }
                if(data.hasNode(node.soul)) {
                    if(data.getNode(node.soul).states.opt(key) != null) {
                        was = data.getNode(node.soul).states.getLong(key);
                    }
                    known = data.getNode(node.soul).values.opt(key) == null ? 0 : data.getNode(node.soul).values.opt(key);
                }

                HAMResult ham = ham(machine, state, was, value, known);
                if(!ham.incoming) {
                    if(ham.defer) {
                        System.out.println("DEFER: " + key + " " + value);
                        // Hack for accessing value in lambda without making the variable final
                        StorageBackend[] graph = new StorageBackend[] {data};
                        Utils.setTimeout(() -> mix(node, graph[0]), (int) (state - System.currentTimeMillis()));
                    }
                    continue;
                }

                if(diff == null) {
                    diff = new InMemoryGraph();
                }

                if(!diff.hasNode(node.soul)) {
                    diff.addNode(node.soul, Utils.newNode(node.soul, new JSONObject()));
                }

                if(!data.hasNode(node.soul)) {
                    data.addNode(node.soul, Utils.newNode(node.soul, new JSONObject()));
                }

                data.getNode(node.soul).values.put(key, value);
                diff.getNode(node.soul).values.put(key, value);

                diff.getNode(node.soul).states.put(key, state);
                data.getNode(node.soul).states.put(key, state);
            }
        }

        return diff;
    }

    public static InMemoryGraph mix(Node incomingNode, StorageBackend data) {
        long machine = System.currentTimeMillis();
        InMemoryGraph diff = null;

        for(String key : incomingNode.values.keySet()) {
            Object value = incomingNode.values.get(key);
            if ("_".equals(key)) { continue; }
            long state = incomingNode.states.getLong(key);
            long was = -1;
            Object known = null;
            if(data == null) {
                data = new InMemoryGraph();
            }
            if(data.hasNode(incomingNode.soul)) {
                if(data.getNode(incomingNode.soul).states.opt(key) != null) {
                    was = data.getNode(incomingNode.soul).states.getLong(key);
                }
                known = data.getNode(incomingNode.soul).values.opt(key) == null ? 0 : data.getNode(incomingNode.soul).values.opt(key);
            }

            HAMResult ham = ham(machine, state, was, value, known);
            if(!ham.incoming) {
                if(ham.defer) {
                    System.out.println("DEFER: " + key + " " + value);
                    // Hack for accessing value in lambda without making the variable final
                    StorageBackend[] graph = new StorageBackend[] {data};
                    Utils.setTimeout(() -> mix(incomingNode, graph[0]), (int) (state - System.currentTimeMillis()));
                }
                continue;
            }

            if(diff == null) {
                diff = new InMemoryGraph();
            }

            if(!diff.hasNode(incomingNode.soul)) {
                diff.addNode(incomingNode.soul, Utils.newNode(incomingNode.soul, new JSONObject()));
            }

            if(!data.hasNode(incomingNode.soul)) {
                data.addNode(incomingNode.soul, Utils.newNode(incomingNode.soul, new JSONObject()));
            }

            data.getNode(incomingNode.soul).values.put(key, value);
            diff.getNode(incomingNode.soul).values.put(key, value);

            diff.getNode(incomingNode.soul).states.put(key, state);
            data.getNode(incomingNode.soul).states.put(key, state);
        }
        return diff;
    }
}
