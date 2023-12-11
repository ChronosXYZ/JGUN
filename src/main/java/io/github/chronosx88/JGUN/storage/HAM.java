package io.github.chronosx88.JGUN.storage;

import io.github.chronosx88.JGUN.models.graph.NodeValue;

public class HAM {
    public static class HAMResult {
        public boolean defer = false; // Defer means that the current state is greater than our computer time, and should only be processed when our computer time reaches this state
        public boolean historical = false; // Historical means the old state. This is usually ignored.
        public boolean incoming = false; // Leave incoming value
        public boolean current = false; // Leave current value
    }

    public static HAMResult ham(long machineState,
                                long incomingState,
                                long currentState,
                                NodeValue incomingValue,
                                NodeValue currentValue) {
        HAMResult result = new HAMResult();

        if (machineState < incomingState) {
            // the incoming value is outside the boundary of the machine's state, it must be reprocessed in another state.
            result.defer = true;
            return result;
        } else if (currentState > incomingState) {
            // the incoming value is within the boundary of the machine's state, but not within the range.
            result.historical = true;
            result.current = true;
            return result;
        } else if (currentState < incomingState) {
            // the incoming value is within both the boundary and the range of the machine's state.
            result.incoming = true;
            return result;
        } else { // if incoming state and current state is the same
            result.incoming = false; // don't update local value with incoming value if state is the same
            return result;
        }
    }
}
