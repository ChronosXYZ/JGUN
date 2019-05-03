package io.github.chronosx88.GunJava;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

public class HAM {
    static class HAMResult {
        public boolean defer = false;
        public boolean historical = false;
        public boolean converge = false;
        public boolean incoming = false;
        public boolean current = false;
        public boolean state = false;
        public String err = null;
    }

    public static HAMResult ham(long machineState, long incomingState, long currentState, Object incomingValue, Object currentValue) {
        HAMResult result = new HAMResult();

        if(machineState < incomingState) {
            result.defer = true;
            return result;
        }
        if(incomingState < currentState){
            result.historical = true;
            return result;
        }
        if(currentState < incomingState) {
            result.converge = true;
            result.incoming = true;
            return result;
        }
        if(incomingState == currentState) {
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
        result.err = "Invalid CRDT Data: "+ incomingValue +" to "+ currentValue +" at "+ incomingState +" to "+ currentState +"!";
        return result;
    }

    public static Graph mix(Graph change, Graph graph) {
        long machine = System.currentTimeMillis();
        Graph diff = null;
        for(Map.Entry<String, Node> entry : change.entries()) {
            Node node = entry.getValue();
            for(String key : node.values.keySet()) {
                Object value = node.values.get(key);
                if ("_".equals(key)) { continue; }
                long state = node.states.getLong(key);
                long was = -1;
                Object known = null;
                if(graph == null) {
                    graph = new Graph();
                }
                if(graph.hasNode(node.soul)) {
                    if(graph.getNode(node.soul).states.opt(key) != null) {
                        was = graph.getNode(node.soul).states.getLong(key);
                    }
                    known = graph.getNode(node.soul).values.opt(key) == null ? 0 : graph.getNode(node.soul).values.opt(key);
                }

                HAMResult ham = ham(machine, state, was, value, known);
                if(!ham.incoming) {
                    if(ham.defer) {
                        System.out.println("DEFER: " + key + " " + value);
                        // FIXME
                    }
                    continue;
                }

                if(diff == null) {
                    diff = new Graph();
                }

                if(!diff.hasNode(node.soul)) {
                    diff.addNode(node.soul, Utils.newNode(node.soul, new JSONObject()));
                }

                if(!graph.hasNode(node.soul)) {
                    graph.addNode(node.soul, Utils.newNode(node.soul, new JSONObject()));
                }

                graph.getNode(node.soul).values.put(key, value);
                diff.getNode(node.soul).values.put(key, value);

                diff.getNode(node.soul).states.put(key, state);
                graph.getNode(node.soul).states.put(key, state);
            }
        }

        return diff;
    }
}
