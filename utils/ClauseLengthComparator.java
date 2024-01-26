package utils;

import problem.Clause;

import java.util.Comparator;

public class ClauseLengthComparator implements Comparator<Clause> {
    @Override
    public int compare(Clause c1, Clause c2) {
        return Integer.compare(c1.getLength(), c2.getLength());
    }
}
