package problem;

import system.ConflictResolver;
import system.DecisionMaker;
import system.TrailManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Clause {
    // MAIN FIELDS
    private int id;
    private HashSet<Literal> disjunction;
    private HashSet<Literal> watchedLiterals;

    // UTILS
    private Literal implied;
    private boolean satisfied;
    private int trueLiteralsCounter;
    private int length;

    public Clause(int id, List<Literal> disjunction) {
        this.id = id;
        this.disjunction = new HashSet<>();
        this.watchedLiterals = new HashSet<>();
        this.satisfied = false;
        this.trueLiteralsCounter = 0;
        this.length = disjunction.size();

        for (Literal l : disjunction) {
            this.disjunction.add(l);
            l.foundIn(this);

            if (disjunction.indexOf(l) < 2) {
                this.watchedLiterals.add(l);
                l.watchedIn(this);
            }
        }

        if (this.disjunction.size() == 1)
            this.implied = disjunction.get(0);

    }

    public Clause(List<Literal> disjunction) {
        this.id = 0;
        this.disjunction = new HashSet<>(disjunction);
    }

    public void updateWatched() {
        List<Literal> falseWatched = this.watchedLiterals.stream().filter(Literal::isFalsified).toList();
        List<Literal> availableLiterals = new ArrayList<>(this.disjunction.stream()
                .filter(literal -> !this.watchedLiterals.contains(literal))
                .filter(literal -> !literal.isFalsified())
                .toList());

        List<Literal> toRemove = new ArrayList<>();

        if(!falseWatched.isEmpty()) {
            for (Literal l : falseWatched) {
                if (!availableLiterals.isEmpty()) {
                    Literal newWatched = availableLiterals.remove(0);
                    this.watchedLiterals.add(newWatched);
                    newWatched.watchedIn(this);
                    l.removeWatcher(this);
                    toRemove.add(l);
                }
            }

            toRemove.forEach(this.watchedLiterals::remove);

            this.analyzeWatched();
        }
    }

    public boolean updateWatched(Literal falseWatched) {
        List<Literal> availableLiterals = this.disjunction.stream()
                .filter(literal -> !this.watchedLiterals.contains(literal))
                .filter(literal -> !literal.isFalsified())
                .toList();

        if(!availableLiterals.isEmpty()) {
            Literal replaceWith = availableLiterals.get(0);
            this.watchedLiterals.remove(falseWatched);
            this.watchedLiterals.add(replaceWith);
            replaceWith.watchedIn(this);
            this.analyzeWatched();
            return true;
        } else {
            this.analyzeWatched();
            return false;
        }
    }

    public void updateLiteralVSIDS() {
        this.disjunction.forEach(Literal::updateScoreVSIDS);
    }

    public HashSet<Literal> getDisjunction() {
        return this.disjunction;
    }

    public HashSet<Literal> getWatchedLiterals() {
        return this.watchedLiterals;
    }

    public Literal getImplied() {
        return this.implied;
    }

    public boolean isSatisfied() {
        return this.satisfied;
    }

    public int getLength() {
        return this.length;
    }

    public void setImplied(Literal implied) {
        this.implied = implied;
    }

    public void setTrueLiteralsCounter(int trueLiteralsCounter) {
        this.trueLiteralsCounter += trueLiteralsCounter;

        boolean previous = this.satisfied;
        this.satisfied = this.trueLiteralsCounter > 0;

        if(this.satisfied && !previous) {
            TrailManager.getTrailManager().updateCounter(1);
            DecisionMaker.getDecisionMaker().satisfiedClause(this);

            for(Literal l: this.disjunction)
                l.updateScoreHeuristicC(-1);
        }
        else if(!this.satisfied && previous) {
            TrailManager.getTrailManager().updateCounter(-1);

            for(Literal l: this.disjunction)
                l.updateScoreHeuristicC(+1);
        }
    }

    @Override
    public String toString() {
        if(this.disjunction.isEmpty())
            return "□";

        String res = "[";

        List<Literal> toPrint = new ArrayList<>(this.disjunction);
        for(Literal l: toPrint) {
            res += l.toString();

            if(toPrint.indexOf(l) < toPrint.size() - 1)
                res += " ∨ ";
        }

        return res + "]";
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    // PRIVATE METHODS
    private void analyzeWatched() {
        List<Literal> nonFalseWatched = this.watchedLiterals.stream().filter(literal -> !literal.isFalsified()).toList();
        int nonFalseWatchedCounter = nonFalseWatched.size();

        switch(nonFalseWatchedCounter) {
            case 0:
                TrailManager.getTrailManager().removeToPropagate(this.implied);
                this.implied = null;
                TrailManager.getTrailManager().removeFromQueue(this);
                ConflictResolver.getConflictResolver().setConflictClause(this);
                break;
            case 1:
                if(!this.satisfied) {
                    this.implied = nonFalseWatched.get(0);
                    TrailManager.getTrailManager().addToPropagate(this.implied);
                    TrailManager.getTrailManager().addToQueue(this);
                }
                break;
            case 2:
                if(this.implied != null) {
                    TrailManager.getTrailManager().removeToPropagate(this.implied);
                    TrailManager.getTrailManager().removeFromQueue(this);
                    this.implied = null;
                }
            default:
                break;
        }
    }
}
