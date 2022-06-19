package expression;

import expression.types.Type;

import java.math.BigDecimal;

public class Min<T> extends BinaryOperation<T> {
    public Min(Parent<T> left, Parent<T> right) {
        super(left, right);
    }

    @Override
    public T evaluate(T x, T y, Type<T> type) {
        return type.min(x, y);
    }

    @Override
    public BigDecimal eval(BigDecimal x, BigDecimal y) {
        return x.min(y);
    }

    @Override
    public int eval(int x, int y) {
        return Math.min(x, y);
    }

    @Override
    public String getBinaryOperand() {
        return "min";
    }

    @Override
    public int getPriority() {
        return -1;
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
