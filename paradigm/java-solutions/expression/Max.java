package expression;

import expression.types.Type;

import java.math.BigDecimal;

public class Max<T> extends BinaryOperation<T> {
    public Max(Parent<T> left, Parent<T> right) {
        super(left, right);
    }

    @Override
    public T evaluate(T x, T y, Type<T> type) {
        return type.max(x, y);
    }

    @Override
    public BigDecimal eval(BigDecimal x, BigDecimal y) {
        return x.max(y);
    }

    @Override
    public int eval(int x, int y) {
        return Math.max(x, y);
    }

    @Override
    public String getBinaryOperand() {
        return "max";
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
