package expression.exceptions;

import expression.MinusElement;
import expression.Parent;

public class CheckedNegate extends MinusElement {
    public CheckedNegate(Parent innerElement) {
        super(innerElement);
    }

    @Override
    public int eval(int x) {
        if (x >= Integer.MIN_VALUE + 1) {
            return -x;
        }
        throw new TooBigIntegerException("Overflow negate error!");
    }
}
