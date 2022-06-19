package expression;

import expression.types.Type;

import java.math.BigDecimal;

public class Shift<T> extends BinaryOperation<T> {
    private final String shift;

    public Shift(Parent<T> left, Parent<T> right, String shift) {
        super(left, right);
        this.shift = shift;
    }

    @Override
    public T evaluate(T x, T y, Type<T> type) {
        throw new IllegalArgumentException();
    }

    @Override
    public int evaluate(int x) {
        throw new IllegalArgumentException();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean isSideDependence() {
        return true;
    }

    @Override
    public boolean mustWrapRight() {
        return false;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        if (shift.equals("<<")) {
            return left.evaluate(x, y, z) << right.evaluate(x, y, z);
        }
        if (shift.equals(">>>")) {
            return left.evaluate(x, y, z) >>> right.evaluate(x, y, z);
        }
        return left.evaluate(x, y, z) >> right.evaluate(x, y, z);
    }

    @Override
    public BigDecimal eval(BigDecimal x, BigDecimal y) {
        throw new IllegalArgumentException();
    }

    @Override
    public int eval(int x, int y) {
        throw new IllegalArgumentException();
    }

    @Override
    public String getBinaryOperand() {
        return shift;
    }
}
