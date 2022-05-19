package expression;

import java.math.BigDecimal;

public class Subtract extends BinaryOperation {
    public Subtract(Parent left, Parent right) {
        super(left, right);
    }

    @Override
    public String getBinaryOperand() {
        return "-";
    }

    @Override
    public BigDecimal eval(BigDecimal x, BigDecimal y) {
        return x.subtract(y);
    }

    @Override
    public int eval(int x, int y) {
        return x - y;
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean isSideDependence() {
        return true;
    }

    @Override
    public boolean mustWrapRight() {
        return false;
    }
}
