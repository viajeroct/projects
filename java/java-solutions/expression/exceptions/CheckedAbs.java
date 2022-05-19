package expression.exceptions;

import expression.Parent;
import expression.UnaryOperation;

public class CheckedAbs extends UnaryOperation {
    public CheckedAbs(Parent innerElement) {
        super(innerElement);
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
