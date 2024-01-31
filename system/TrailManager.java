package system;

import problem.Clause;
import problem.Literal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TrailManager {
    private static TrailManager trailManager;

    // MAIN FIELDS
    private Map<Integer, Stack<Literal>> trail;
    private HashSet<Clause> set;
    private int level;

    // UTILS
    private List<Clause> queue;
    private HashSet<Literal> toPropagate;
    private Map<Literal, Clause> trailMap;
    private int satisfiedClausesCounter;
    private boolean unsat;
    private HashSet<Clause> originalSet;
    private Map<Integer, Literal> litToObj;

    private TrailManager() {}

    public static TrailManager getTrailManager() {
        if(trailManager == null)
            trailManager = new TrailManager();

        return trailManager;
    }

    public void initialize(String fileName) {
        this.trail = new HashMap<>();
        this.litToObj = new HashMap<>();
        this.set = new HashSet<>(this.parseFile(fileName));
        this.level = 0;
        this.queue = new ArrayList<>();
        this.toPropagate = new HashSet<>();
        this.trailMap = new HashMap<>();
        this.satisfiedClausesCounter = 0;
        this.originalSet = new HashSet<>(this.set);

        System.out.println("The transition system is:\n" + this + "\nThere are " + this.set.size() + " clauses and " +
                this.litToObj.keySet().size() + " distinct literals");

        this.unsat = false;
    }
    public boolean isSat() {
        return this.satisfiedClausesCounter == this.set.size();
    }

    public boolean alternativeSat() {
        for(Clause c: this.set)
            if(!c.isSatisfied())
                return false;

        return true;
    }

    public boolean isUnsat() {
        return this.unsat;
    }

    public boolean canPropagate() {
        return !this.queue.isEmpty();
    }

    public void unitClausePropagation() {
        this.set.stream()
                .filter(clause -> clause.getImplied() != null)
                .forEach(clause -> this.queue.add(clause));
    }

    public void propagate() {
        Clause justification = this.queue.remove(0);;
        Literal implied = justification.getImplied();
        this.toPropagate.remove(implied);

        List<Clause> toRemove = new ArrayList<>();
        for(Clause c: this.queue)
            if(c.getImplied() == implied) {
                c.setImplied(null);
                toRemove.add(c);
            }

        this.queue.removeAll(toRemove);

        if(!this.trail.containsKey(this.level))
            this.trail.put(this.level, new Stack<>());

        this.trailMap.put(implied, justification);
        justification.setImplied(null);

        System.out.println("\n[PROPAGATION] Propagate: " + implied + "_{" + justification + "}");
        this.putInTrail(implied);
    }

    public void decide() {
        this.level += 1;
        this.trail.put(this.level, new Stack<>());

        Literal decided = DecisionMaker.getDecisionMaker().makeDecision();

        System.out.println("\n[DECISION] Decide: " + decided);
        this.putInTrail(decided);
    }

    public void addToQueue(Clause c) {
        this.queue.add(c);
    }

    public void addToPropagate(Literal l) {
        this.toPropagate.add(l);
    }

    public void removeFromQueue(Clause c) {
        this.queue.remove(c);
    }

    public void removeToPropagate(Literal l) {
        this.toPropagate.remove(l);
    }

    public boolean aboutToPropagate(Literal l) {
        return this.toPropagate.contains(l);
    }

    public void updateCounter(int update) {
        this.satisfiedClausesCounter += update;
    }

    public boolean isInputClause(Clause c) {
        return this.originalSet.contains(c);
    }

    public Stack<Literal> getCurrentLevel() {
        return this.trail.get(this.level);
    }

    public Clause getJustification(Literal l) {
        return this.trailMap.get(l);
    }

    public Map<Integer, Stack<Literal>> getTrails() {
        return this.trail;
    }

    public Stack<Literal> getTrail() {
        Stack<Literal> trail = new Stack<>();

        for(int level: this.trail.keySet())
            trail.addAll(this.trail.get(level));

        return trail;
    }

    public HashSet<Clause> getSet() {
        return this.set;
    }

    public Collection<Literal> getLiterals() {
        return this.litToObj.values();
    }

    @Override
    public String toString() {
        String res = "M: [";

        for(Literal l: getTrail()) {
            Clause justification = this.trailMap.get(l);

            res += l.toString();

            if(justification != null)
                res += "_{" + justification.getDisjunction() + "}";

            res += ", ";
        }

        res += "]";

        return res;
    }

    // PRIVATE METHODS
    private void putInTrail(Literal toPut) {
        Stack<Literal> currentLevel = this.trail.get(this.level);
        currentLevel.add(toPut);
        this.trail.put(this.level, currentLevel);

        DecisionMaker.getDecisionMaker().nonDecidableLiteral(toPut);
        toPut.updateCounter(1);

        Literal opposite = toPut.getOpposite();

        if(opposite != null) {
            opposite.setFalsified(true);
            opposite.setFalseLevel(this.level);
            DecisionMaker.getDecisionMaker().nonDecidableLiteral(opposite);
        }

        System.out.println("[UPDATE] The transition system is now:\n" + this + "\nSatisfied clauses/Total clauses: " +
                this.satisfiedClausesCounter + "/" + this.set.size());

        if(ConflictResolver.getConflictResolver().isConflict())
            if(this.level == 0) {
                ConflictResolver.getConflictResolver().proofGeneration();
                this.unsat = true;
            } else {
                System.out.println("\n[CONFLICT] Found conflict clause: " + ConflictResolver.getConflictResolver().getConflictClause()
                        + ConflictResolver.getConflictResolver().getConflictClause().getWatchedLiterals());
                queue.clear();
                toPropagate.clear();

                Clause assertionClause = ConflictResolver.getConflictResolver().manageConflict();
                System.out.println("\n[LEARNING] Learning clause: " + assertionClause);

                this.backJump(assertionClause.getImplied(), assertionClause);
                DecisionMaker.getDecisionMaker().updateVSIDS();
            }
    }

    private void backJump(Literal assertionLiteral, Clause assertionClause) {
        int jumpTo = 0;
        for(Literal l: assertionClause.getDisjunction()) {
            if (l == assertionLiteral)
                continue;

            if (l.getFalseLevel() > jumpTo)
                jumpTo = l.getFalseLevel();
        }

        for(int i = jumpTo+1; i <= this.level; i++) {
            Stack<Literal> toRemove = this.trail.remove(i);
            toRemove.forEach(literal -> {
                literal.updateCounter(-1);
                DecisionMaker.getDecisionMaker().decidableLiteral(literal);

                Literal opposite = literal.getOpposite();
                if(opposite != null)
                    opposite.setFalsified(false);

                this.trailMap.remove(literal);
            });
        }

        this.set.forEach(Clause::updateWatched);

        this.level = jumpTo;

        this.set.add(assertionClause);
        this.queue.add(0, assertionClause);
    }

    private List<Clause> parseFile(String fileName) {
        List<Clause> clausesInFile = new ArrayList<>();

        try {
            // CLAUSES RETRIEVAL FROM INPUT FILE
            File file = new File(fileName);
            Scanner reader = new Scanner(file);

            while (reader.hasNextLine()){
                String line = reader.nextLine();

                // some files terminate with empty lines
                if(line.isEmpty())
                    continue;

                // files from cs.ubc.ca files end with % \n 0
                if(line.startsWith("%") || line.startsWith("0"))
                    continue;

                // .cnf files begin with description (line starting with c) and parameters (line starting with p)
                if(line.startsWith("c") || line.startsWith("p"))
                    continue;

                ArrayList<String> symbols = new ArrayList<>(List.of(line.split(" ")));

                // first clauses in cs.ubc.ca files begin with an empty space
                symbols.remove("");

                // .cnf files separate clauses with 0
                symbols.remove("0");

                ArrayList<Literal> clause = new ArrayList<>();

                for(String s: symbols) {
                    int key = Integer.parseInt(s);

                    // for each symbol a single object is kept
                    if(this.litToObj.containsKey(key))
                        clause.add(this.litToObj.get(key));
                    else {
                        Literal newLiteral = new Literal(key);
                        this.litToObj.put(key, newLiteral);

                        Literal opposite = this.litToObj.get(-(newLiteral.getSymbol()));
                        if(opposite != null) {
                            newLiteral.setOpposite(opposite);
                            opposite.setOpposite(newLiteral);
                        }

                        clause.add(newLiteral);
                    }
                }

                Clause newClause = new Clause(clausesInFile.size() + 1, clause);
                clausesInFile.add(newClause);
            }

            reader.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            System.exit(-1);
        }

        return clausesInFile;
    }
}
