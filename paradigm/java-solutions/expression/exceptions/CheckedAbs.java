package expression.exceptions;

import expression.Parent;
import expression.UnaryOperation;
import expression.types.Type;

public class CheckedAbs<T> extends UnaryOperation<T> {
    public CheckedAbs(Parent<T> innerElement) {
        super(innerElement);
    }

    @Override
    public T evaluate(T x, Type<T> type) {
        throw new IllegalArgumentException();
    }

    @Override
    public int eval(int x) {
        if (x >= 0) {
            return x;
        } else if (x >= Integer.MIN_VALUE + 1) {
            return -x;
        }
        throw new TooBigIntegerException("Overflow abs error!");
    }

    @Override
    public String getPrefix() {
        return "abs";
    }

    @Override
    public int getPriority() {
        return 11;
    }
}
