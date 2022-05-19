package expression;

import java.math.BigDecimal;

public class Divide extends BinaryOperation {
    public Divide(Parent left, Parent right) {
        super(left, right);
    }

    @Override
    public BigDecimal eval(BigDecimal x, BigDecimal y) {
        return x.divide(y);
    }

    @Override
    public int eval(int x, int y) {
        return x / y;
    }

    @Override
    public String getBinaryOperand() {
        return "/";
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean isSideDependence() {
        return true;
    }

    @Override
    public boolean mustWrapRight() {
        return true;
    }
}
