package system;

import problem.Clause;
import problem.Literal;

import java.util.*;

public class ConflictResolver {
    private static ConflictResolver conflictResolver;

    // MAIN FIELDS
    private Clause conflictClause;
    private Map<Clause, List<Clause>> resolutionSteps;

    private ConflictResolver() {}

    public static ConflictResolver getConflictResolver() {
        if(conflictResolver == null)
            conflictResolver = new ConflictResolver();

        return conflictResolver;
    }

    public boolean isConflict() {
        return this.conflictClause != null;
    }

    public boolean conflictHappened() {
        return this.resolutionSteps != null;
    }

    public Clause manageConflict() {
        Stack<Literal> currentLevel = TrailManager.getTrailManager().getCurrentLevel();
        int prevId = TrailManager.getTrailManager().getSet().size();
        int levelSize = currentLevel.size();
        int start = 1;

        Literal assertionLiteral = null;
        Clause resolvent = null;

        while(assertionLiteral == null) {
            Literal inTrail = currentLevel.get(levelSize - start);
            Literal opposite = inTrail.getOpposite();

            if(opposite != null && !this.conflictClause.getDisjunction().contains(opposite)) {
                start += 1;
                continue;
            }

            Clause resolveWith = TrailManager.getTrailManager().getJustification(inTrail);

            resolvent = this.explain(this.conflictClause, resolveWith, inTrail);
            resolvent.updateLiteralVSIDS();

            System.out.println("\n[EXPLAIN] " + this.conflictClause + " | " + resolveWith + " -> " + resolvent);

            if(this.resolutionSteps == null)
                this.resolutionSteps = new HashMap<>();

            this.resolutionSteps.put(resolvent, List.of(this.conflictClause, resolveWith));
            this.conflictClause = resolvent;

            assertionLiteral = getAsserted(currentLevel);

            start += 1;
        }

        List<Literal> resolventLiterals = new ArrayList<>(resolvent.getDisjunction());
        Clause assertionClause = new Clause(prevId + 1, resolventLiterals);
        assertionClause.setImplied(assertionLiteral);

        this.conflictClause = null;
        return assertionClause;
    }

    public Clause getConflictClause() {
        return this.conflictClause;
    }

    public void setConflictClause(Clause conflictClause) {
        this.conflictClause = conflictClause;
    }

    // PRIVATE METHODS
    private Clause explain(Clause parent1, Clause parent2, Literal toRemove) {
        HashSet<Literal> parent1Literals = new HashSet<>(parent1.getDisjunction());
        HashSet<Literal> parent2Literals = new HashSet<>(parent2.getDisjunction());

        parent1Literals.remove(toRemove.getOpposite());
        parent2Literals.remove(toRemove);

        parent1Literals.addAll(parent2Literals);
        List<Literal> resolvent = new ArrayList<>(parent1Literals);

        return new Clause(resolvent);
    }

    private Literal getAsserted(List<Literal> currentLevel) {
        List<Literal> falseLiterals = this.conflictClause.getDisjunction().stream()
                .filter(l -> currentLevel.contains(l.getOpposite()))
                .toList();

        System.out.println(TrailManager.getTrailManager().getTrails());

        if(falseLiterals.size() > 1)
            return null;

        return falseLiterals.get(0);
    }
}
