package problem;

import java.util.ArrayList;
import java.util.List;

public class Variable {

    private final int symbol;
    private Literal positive;
    private Literal negative;
    private int scoreVSIDS;

    public Variable(int symbol)  {
        this.symbol = Math.abs(symbol);
        this.scoreVSIDS = 0;

        if(symbol > 0)
            this.positive = new Literal(symbol, this);
        else
            this.negative = new Literal(symbol, this);
    }

    public Literal getLiteral(int symbol) {
        if(symbol > 0) {
            if (this.positive == null) {
                this.positive = new Literal(symbol, this);
                this.negative.setOpposite(this.positive);
                this.positive.setOpposite(this.negative);
            }
            return this.positive;
        } else {
            if (this.negative == null) {
                this.negative = new Literal(symbol, this);
                this.positive.setOpposite(this.negative);
                this.negative.setOpposite(this.positive);
            }
            return this.negative;
        }
    }

    public Literal decideLiteral() {
        if (this.positive == null) {
            return this.negative;
        }

        return this.positive;
    }

    public List<Literal> getLiteralsList() {
        List<Literal> notNull = new ArrayList<>();

        if(this.positive != null)
            notNull.add(this.positive);

        if(this.negative != null)
            notNull.add(this.negative);

        return notNull;
    }

    public int getScoreVSIDS() {
        return this.scoreVSIDS;
    }

    public void updateScoreVSIDS() {
        this.scoreVSIDS += 1;
    }

    @Override
    public int hashCode() {
        return this.symbol;
    }
}
