package system;

import problem.Clause;

public class ConflictResolver {
    private static ConflictResolver conflictResolver;

    // MAIN FIELDS
    public Clause conflictClause;

    private ConflictResolver() {}

    public static ConflictResolver getConflictResolver() {
        if(conflictResolver == null)
            conflictResolver = new ConflictResolver();

        return conflictResolver;
    }
}
