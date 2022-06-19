package expression;

import expression.types.Type;

import java.math.BigDecimal;

public class Divide<T> extends BinaryOperation<T> {
    public Divide(Parent<T> left, Parent<T> right) {
        super(left, right);
    }

    @Override
    public T evaluate(T x, T y, Type<T> type) {
        return type.divide(x, y);
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
