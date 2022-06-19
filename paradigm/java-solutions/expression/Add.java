package expression;

import expression.types.Type;

import java.math.BigDecimal;

public class Add<T> extends BinaryOperation<T> {
    public Add(Parent<T> left, Parent<T> right) {
        super(left, right);
    }

    @Override
    public T evaluate(T x, T y, Type<T> type) {
        return type.add(x, y);
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
