package expression;

import expression.types.Type;

import java.math.BigDecimal;

public class Multiply<T> extends BinaryOperation<T> {
    public Multiply(Parent<T> left, Parent<T> right) {
        super(left, right);
    }

    @Override
    public T evaluate(T x, T y, Type<T> type) {
        return type.multiply(x, y);
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
