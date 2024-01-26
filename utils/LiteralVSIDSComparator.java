package utils;

import problem.Literal;

import java.util.Comparator;

public class LiteralVSIDSComparator implements Comparator<Literal> {
    @Override
    public int compare(Literal l1, Literal l2) {
        return Integer.compare(l1.getScoreVSIDS(), l2.getScoreVSIDS());
    }
}
