package io.github.chronosx88.JGUN;

public class HAM {
    public static class HAMResult {
        public boolean defer = false; // Defer means that the current state is greater than our computer time, and should only be processed when our computer time reaches this state
        public boolean historical = false; // Historical means the old state. This is usually ignored.
        public boolean incoming = false; // Leave incoming value
        public boolean current = false; // Leave current value
    }

    public static HAMResult ham(long machineState, long incomingState, long currentState, Object incomingValue, Object currentValue) throws IllegalArgumentException {
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
            if (incomingValue.equals(currentValue)) {
                result.current = true;
                return result;
            }
            result.incoming = true; // always update local value with incoming value if state is the same
            return result;
        }
    }
}
