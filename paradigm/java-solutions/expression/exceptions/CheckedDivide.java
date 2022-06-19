package expression.exceptions;

import expression.Divide;
import expression.Parent;

public class CheckedDivide<T> extends Divide<T> {
    public CheckedDivide(Parent<T> left, Parent<T> right) {
        super(left, right);
    }

    @Override
    public int eval(int x, int y) {
        if (x == Integer.MIN_VALUE && y == -1) {
            throw new TooBigIntegerException("Overflow divide error!");
        }
        if (y != 0) {
            return x / y;
        }
        throw new TooBigIntegerException("Overflow divide error!");
    }
}
