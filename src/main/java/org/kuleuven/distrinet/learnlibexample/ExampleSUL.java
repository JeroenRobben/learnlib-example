package org.kuleuven.distrinet.learnlibexample;

import de.learnlib.api.SUL;

/**
 * Example of a hard-coded three-state system, based on example from Ramon Janssen
 */
public class ExampleSUL implements SUL<String, Integer> {
    private enum State{s0,s1,s2};
    private State currentState;
    private static final boolean VERBOSE = true;

    @Override
    public void pre() {
        // add any code here that should be run at the beginning of every 'session',
        // i.e. put the system in its initial state
        if (VERBOSE) {
            System.out.println("Starting SUL");
        }
        currentState = State.s0;
    }

    @Override
    public void post() {
        // add any code here that should be run at the end of every 'session'
        if (VERBOSE) {
            System.out.println("Shutting down SUL");
        }
    }

    @Override
    public Integer step(String input) throws UnsupportedOperationException {
        State previousState = this.currentState;
        Integer output = makeTransition(input);
        State nextState = this.currentState;
        if (VERBOSE) {
            System.out.println(previousState + " --" + input + "/" + Integer.toString(output) + "-> " + nextState);
        }
        return output;
    }

    /**
     * The behaviour of the SUL. It takes one input, and returns an output. It now
     * contains a hardcoded state-machine (so the result is easy to check). To learn
     * an external program/system, connect this method to the SUL (e.g. via sockets
     * or stdin/stdout) and make it perform an actual input, and retrieve an actual
     * output.
     * @param input
     * @returnhard-coded
     */
    public Integer makeTransition(String input) {
        switch (currentState) {
            case s0:
                switch(input) {
                    case "symbolA":
                        currentState = State.s1;
                        return 11111;
                    case "symbolB":
                        currentState = State.s2;
                        return 22222;
                    case "symbolC":
                        return 33333;
                }
            case s1:
                switch(input) {
                    case "symbolA":
                        return 44444;
                    case "symbolB":
                        currentState = State.s2;
                        return 55555;
                    case "symbolC":
                        return 66666;
                }
            case s2:
                switch(input) {
                    case "symbolA":
                        return 77777;
                    case "symbolB":
                        currentState = State.s0;
                        return 88888;
                    case "symbolC":
                        return 99999;
                }
        }
        throw new UnsupportedOperationException(new IllegalArgumentException("Argument '" + input + "' was not handled"));
    }
}

