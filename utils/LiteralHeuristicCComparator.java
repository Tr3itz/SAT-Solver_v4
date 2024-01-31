package utils;

import problem.Literal;

import java.util.Comparator;

public class LiteralHeuristicCComparator implements Comparator<Literal> {
    @Override
    public int compare(Literal l1, Literal l2) {
        return Integer.compare(l1.getScoreHeuristicC(), l2.getScoreHeuristicC());
    }
}