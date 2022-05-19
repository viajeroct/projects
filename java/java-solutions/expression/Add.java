package expression;

import java.math.BigDecimal;

public class Add extends BinaryOperation {
    public Add(Parent left, Parent right) {
        super(left, right);
    }

    @Override
    public BigDecimal eval(BigDecimal x, BigDecimal y) {
        return x.add(y);
    }

    @Override
    public int eval(int x, int y) {
        return x + y;
    }

    @Override
    public String getBinaryOperand() {
        return "+";
    }

    @Override
    public int getPriority() {
        return 1;
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
