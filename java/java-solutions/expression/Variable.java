package expression;

import java.math.BigDecimal;

public class Variable extends Parent {
    private final String x;

    public Variable(String x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return x;
    }

    @Override
    public void toStringFast(StringBuilder res) {
        res.append(x);
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int hashCode() {
        return x.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable) {
            Variable cur = (Variable) obj;
            return this.x.equals(cur.x);
        }
        return false;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (this.x) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public BigDecimal evaluate(BigDecimal x) {
        return x;
    }

    @Override
    public String getBinaryOperand() {
        return "";
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public boolean isSideDependence() {
        return false;
    }

    @Override
    public boolean mustWrapRight() {
        return false;
    }
}
