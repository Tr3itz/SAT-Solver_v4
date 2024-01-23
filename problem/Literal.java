package problem;

import java.util.HashSet;

public class Literal {
    // MAIN FIELDS
    private int symbol;
    private boolean isFalsified;

    // UTILS
    private Literal opposite;
    private HashSet<Clause> foundInClauses;
    private HashSet<Clause> watchedInClauses;
    private int scoreVSIDS;
    private int scoreHeuristicB;

    @Override
    public int hashCode() {
        return this.symbol;
    }
}
