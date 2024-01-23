package system;

import problem.Literal;

import java.util.HashSet;
import java.util.TreeSet;

public class DecisionMaker {
    private static DecisionMaker decisionMaker;

    // MAIN FIELDS
    private TreeSet<Literal> orderedByVSIDS;
    private HashSet<Literal> decidable;
    private HashSet<Literal> nonDecidable;

    private DecisionMaker() {}

    public static DecisionMaker getDecisionMaker() {
        if(decisionMaker == null)
            decisionMaker = new DecisionMaker();

        return decisionMaker;
    }
}
