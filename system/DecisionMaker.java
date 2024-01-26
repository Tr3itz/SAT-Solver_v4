package system;

import problem.Clause;
import problem.Literal;
import utils.ClauseLengthComparator;
import utils.LiteralHeuristicBComparator;
import utils.LiteralVSIDSComparator;

import java.util.*;

public class DecisionMaker {
    private static DecisionMaker decisionMaker;

    // MAIN FIELDS
    private List<Literal> orderedByVSIDS;
    private List<Clause> orderedByLength;
    private HashSet<Literal> decidable;
    private HashSet<Literal> nonDecidable;

    // UTILS
    LiteralVSIDSComparator literalVSIDSComparator;
    LiteralHeuristicBComparator literalHeuristicBComparator;
    ClauseLengthComparator clauseLengthComparator;

    private DecisionMaker() {
        this.decidable = new HashSet<>(TrailManager.getTrailManager().getLiterals());
        this.nonDecidable = new HashSet<>();

        this.literalVSIDSComparator = new LiteralVSIDSComparator();
        this.literalHeuristicBComparator = new LiteralHeuristicBComparator();
        this.clauseLengthComparator = new ClauseLengthComparator();

        this.orderedByLength = new ArrayList<>(TrailManager.getTrailManager().getSet());
        this.orderedByLength.sort(this.clauseLengthComparator);
    }

    public static DecisionMaker getDecisionMaker() {
        if(decisionMaker == null)
            decisionMaker = new DecisionMaker();

        return decisionMaker;
    }

    public Literal makeDecision() {
        if(ConflictResolver.getConflictResolver().conflictHappened()) {
            Literal toDecide = this.orderedByVSIDS.remove(0);
            this.orderedByVSIDS.remove(toDecide);
            return toDecide;
        }

        Clause shortest = orderedByLength.get(0);

        List<Literal> candidates = shortest.getDisjunction()
                .stream()
                .filter(this.decidable::contains)
                .sorted(this.literalHeuristicBComparator)
                .toList();

        return candidates.get(candidates.size() - 1);
    }

    public void decidableLiteral(Literal l) {
        this.nonDecidable.remove(l);
        this.decidable.add(l);
    }

    public void nonDecidableLiteral(Literal l) {
        this.decidable.remove(l);
        this.nonDecidable.add(l);

        if(this.orderedByVSIDS != null)
            this.orderedByVSIDS.remove(l);
    }

    public void satisfiedClause(Clause c) {
        try {
            this.orderedByLength.remove(c);
        }
        catch(NullPointerException ignored) {

        }
    }

    public void updateVSIDS() {
        this.orderedByVSIDS = new ArrayList<>(this.decidable);
        this.orderedByVSIDS.sort(this.literalVSIDSComparator);
    }
}
