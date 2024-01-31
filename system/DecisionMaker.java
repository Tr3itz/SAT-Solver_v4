package system;

import problem.Clause;
import problem.Literal;
import utils.ClauseLengthComparator;
import utils.LiteralHeuristicBComparator;
import utils.LiteralHeuristicCComparator;
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
    private final LiteralVSIDSComparator literalVSIDSComparator;
    private final LiteralHeuristicBComparator literalHeuristicBComparator;
    private final LiteralHeuristicCComparator literalHeuristicCComparator;
    private final ClauseLengthComparator clauseLengthComparator;

    private DecisionMaker() {
        this.decidable = new HashSet<>(TrailManager.getTrailManager().getLiterals());
        this.nonDecidable = new HashSet<>();

        this.literalVSIDSComparator = new LiteralVSIDSComparator();
        this.literalHeuristicBComparator = new LiteralHeuristicBComparator();
        this.literalHeuristicCComparator = new LiteralHeuristicCComparator();
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
            return this.orderedByVSIDS.remove(0);
        }

        return this.HeuristicC();
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

    // PRIVATE METHODS
    private Literal HeuristicB() {
        Clause shortest = orderedByLength.get(0);

        List<Literal> candidates = shortest.getDisjunction()
                .stream()
                .filter(this.decidable::contains)
                .sorted(this.literalHeuristicBComparator)
                .toList();

        return candidates.get(candidates.size() - 1);
    }

    private Literal HeuristicC() {
        List<Literal> candidates = new ArrayList<>(this.decidable);
        candidates.sort(this.literalHeuristicCComparator);
        return candidates.get(candidates.size() - 1);
    }

    private Literal randomLiteral() {
        List<Literal> randLiterals = new ArrayList<>(this.decidable);
        Random rand = new Random();
        int upperBound = randLiterals.size();

        return randLiterals.get(rand.nextInt(upperBound));
    }
}
