package system;

import problem.Clause;
import problem.Literal;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class TrailManager {
    private static TrailManager trailManager;

    // MAIN FIELDS
    private Map<Integer, Stack<Literal>> trail;
    private HashSet<Clause> set;
    private int level;

    // UTILS
    private List<Clause> toPropagate;
    private Map<Literal, Clause> trailMap;
    private HashSet<Clause> originalSet;
    private Map<Integer, Literal> litToObj;

    private TrailManager() {}

    public static TrailManager getTrailManager() {
        if(trailManager == null)
            trailManager = new TrailManager();

        return trailManager;
    }

    public void initialize() {

    }

    public void propagate(Literal implied, Clause justification) {

    }

    public void decide() {

    }

    // PRIVATE METHODS
    private void putInTrail(Literal toPut) {

    }

    private HashSet<Clause> parseFile(String fileName) {
        return null;
    }
}
