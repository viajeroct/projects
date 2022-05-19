package expression;

import java.math.BigDecimal;

public class Multiply extends BinaryOperation {
    public Multiply(Parent left, Parent right) {
        super(left, right);
    }

    @Override
    public BigDecimal eval(BigDecimal x, BigDecimal y) {
        return x.multiply(y);
    }

    @Override
    public int eval(int x, int y) {
        return x * y;
    }

    @Override
    public String getBinaryOperand() {
        return "*";
    }

    @Override
    public int getPriority() {
        return 2;
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
