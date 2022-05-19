package expression;

import java.math.BigDecimal;

public class Const extends Parent {
    private final int var;
    private Integer x;
    private BigDecimal bigX;

    public Const(int x) {
        this.x = x;
        this.var = 1;
    }

    public Const(Integer x) {
        this.x = x;
        this.var = 1;
    }

    public Const(BigDecimal x) {
        this.var = 2;
        this.bigX = x;
    }

    @Override
    public int hashCode() {
        return var == 1 ? x.hashCode() : bigX.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Const) {
            Const other = (Const) obj;
            if (var == 1) {
                return this.x.equals(other.x);
            } else {
                return this.bigX.equals(other.bigX);
            }
        }
        return false;
    }

    @Override
    public void toStringFast(StringBuilder res) {
        res.append(toString());
    }

    @Override
    public String toString() {
        return var == 1 ? Integer.toString(x) : bigX.toString();
    }

    @Override
    public int evaluate(int x) {
        return this.x;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return this.x;
    }

    @Override
    public BigDecimal evaluate(BigDecimal x) {
        return bigX;
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

    @Override
    public String getBinaryOperand() {
        return "";
    }
}
