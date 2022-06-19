package expression.types;

import expression.exceptions.DivisionByZero;
import expression.exceptions.TooBigIntegerException;

import static expression.exceptions.CheckedMultiply.save_multiply;
import static expression.exceptions.CheckedAdd.save_add;
import static expression.exceptions.CheckedSubtract.save_sub;

public class IntegerType implements Type<Integer> {
    @Override
    public Integer add(Integer x, Integer y) {
        return save_add(x, y);
    }

    @Override
    public Integer subtract(Integer x, Integer y) {
        return save_sub(x, y);
    }

    @Override
    public Integer multiply(Integer x, Integer y) {
        if (!save_multiply(x, y)) {
            throw new TooBigIntegerException("Overflow for mul!");
        }
        return x * y;
    }

    @Override
    public Integer castToT(int x) {
        return x;
    }

    @Override
    public Integer count(Integer x) {
        return Integer.bitCount(x);
    }

    @Override
    public Integer min(Integer x, Integer y) {
        return Math.min(x, y);
    }

    @Override
    public Integer max(Integer x, Integer y) {
        return Math.max(x, y);
    }

    @Override
    public Integer divide(Integer x, Integer y) {
        if (y == 0) {
            throw new DivisionByZero("Division by zero in IntegerType class.");
        }
        if (x == Integer.MIN_VALUE && y == -1) {
            return null;
        }
        return x / y;
    }

    @Override
    public Integer parse(String data) {
        return Integer.parseInt(data);
    }

    @Override
    public Integer negate(Integer x) {
        if (x == Integer.MIN_VALUE) {
            return null;
        }
        return -x;
    }
}
