package system;

import problem.Clause;
import problem.Literal;
import problem.Variable;
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
    private HashSet<Variable> decidableVar;
    private HashSet<Literal> decidableLit;
    private HashSet<Variable> nonDecidableVar;
    private HashSet<Literal> nonDecidableLit;

    // UTILS
    private final LiteralVSIDSComparator literalVSIDSComparator;
    private final LiteralHeuristicBComparator literalHeuristicBComparator;
    private final LiteralHeuristicCComparator literalHeuristicCComparator;
    private final ClauseLengthComparator clauseLengthComparator;

    private DecisionMaker() {
        this.decidableLit = new HashSet<>(TrailManager.getTrailManager().getLiterals());
        this.nonDecidableLit = new HashSet<>();

        this.decidableVar = new HashSet<>(TrailManager.getTrailManager().getVariables());
        this.nonDecidableVar = new HashSet<>();

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

        return this.HeuristicB();
    }

    public void decidableLiteral(Literal l) {
        this.nonDecidableLit.remove(l);
        this.decidableLit.add(l);
    }

    public void nonDecidableLiteral(Literal l) {
        this.decidableLit.remove(l);
        this.nonDecidableLit.add(l);

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
        this.orderedByVSIDS = new ArrayList<>(this.decidableLit);
        this.orderedByVSIDS.sort(this.literalVSIDSComparator);
    }

    // PRIVATE METHODS
    private Literal HeuristicB() {
        Clause shortest = orderedByLength.get(0);

        List<Literal> candidates = shortest.getDisjunction()
                .stream()
                .filter(this.decidableLit::contains)
                .sorted(this.literalHeuristicBComparator)
                .toList();

        return candidates.get(candidates.size() - 1);
    }

    private Literal HeuristicC() {
        List<Literal> candidates = new ArrayList<>(this.decidableLit);
        candidates.sort(this.literalHeuristicCComparator);
        return candidates.get(candidates.size() - 1);
    }

    private Literal randomLiteral() {
        List<Literal> randLiterals = new ArrayList<>(this.decidableLit);
        Random rand = new Random();
        int upperBound = randLiterals.size();

        return randLiterals.get(rand.nextInt(upperBound));
    }
}
