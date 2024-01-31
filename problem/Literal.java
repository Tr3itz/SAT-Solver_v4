package problem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Literal {
    // MAIN FIELDS
    private int symbol;
    private boolean falsified;

    // UTILS
    private Literal opposite;
    private HashSet<Clause> foundInClauses;
    private HashSet<Clause> watchedInClauses;
    private int scoreVSIDS;
    private int scoreHeuristicB;
    private int scoreHeuristicC;
    private Integer falseLevel;

    public Literal(int symbol) {
        this.symbol = symbol;
        this.falsified = false;
        this.foundInClauses = new HashSet<>();
        this.watchedInClauses = new HashSet<>();
        this.scoreVSIDS = 0;
        this.scoreHeuristicB = 0;
        this.scoreHeuristicC = 0;
    }

    public void foundIn(Clause c) {
        this.foundInClauses.add(c);
        this.scoreHeuristicB += 1;
        this.scoreHeuristicC += 1;
    }

    public void watchedIn(Clause c) {
        this.watchedInClauses.add(c);
    }

    public void removeWatcher(Clause c) {
            this.watchedInClauses.remove(c);
    }

    public void updateCounter(int update) {
        this.foundInClauses.forEach(clause -> clause.setTrueLiteralsCounter(update));
    }

    public int getSymbol() {
        return this.symbol;
    }

    public boolean isFalsified() {
        return this.falsified;
    }

    public Literal getOpposite() {
        return this.opposite;
    }

    public int getScoreVSIDS() {
        return this.scoreVSIDS;
    }

    public int getScoreHeuristicB() {
        return this.scoreHeuristicB;
    }

    public int getScoreHeuristicC() {
        return this.scoreHeuristicC;
    }

    public int getFalseLevel() {
        return this.falseLevel;
    }

    public void setFalsified(boolean falsified) {
        this.falsified = falsified;

        if(falsified) {
            List<Clause> removeWatched = new ArrayList<>();
            for(Clause c: this.watchedInClauses) {
                if(c.updateWatched(this))
                    removeWatched.add(c);
            }

            removeWatched.forEach(this.watchedInClauses::remove);
        }
    }

    public void setOpposite(Literal opposite) {
        this.opposite = opposite;
    }

    public void updateScoreVSIDS() {
        this.scoreVSIDS += 1;
    }

    public void updateScoreHeuristicC(int update) {
        this.scoreHeuristicC += update;
    }

    public void setFalseLevel(int level) {
        this.falseLevel = level;
    }

    @Override
    public String toString() {
        return Integer.toString(this.symbol);
    }

    @Override
    public int hashCode() {
        return this.symbol;
    }
}
