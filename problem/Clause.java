package problem;

import java.util.HashSet;

public class Clause {
    // MAIN FIELDS
    private int id;
    private HashSet<Literal> disjunction;
    private HashSet<Literal> watchedLiterals;

    // UTILS
    private Literal implied;

    @Override
    public int hashCode() {
        return this.id;
    }
}
