package expression;

import expression.types.Type;

import java.math.BigDecimal;

public class Subtract<T> extends BinaryOperation<T> {
    public Subtract(Parent<T> left, Parent<T> right) {
        super(left, right);
    }

    @Override
    public String getBinaryOperand() {
        return "-";
    }

    @Override
    public T evaluate(T x, T y, Type<T> type) {
        return type.subtract(x, y);
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
