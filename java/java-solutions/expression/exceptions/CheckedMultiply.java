package expression.exceptions;

import expression.Multiply;
import expression.Parent;

public class CheckedMultiply extends Multiply {
    public CheckedMultiply(Parent left, Parent right) {
        super(left, right);
    }

    public static boolean save_multiply(int left_value, int right_value) {
        if (left_value == 0 || right_value == 0) {
            return true;
        }
        if (right_value > 0) {
            return left_value <= Integer.MAX_VALUE / right_value &&
                    left_value >= Integer.MIN_VALUE / right_value;
        } else {
            return left_value >= Integer.MAX_VALUE / right_value &&
                    (right_value == -1 ||
                            left_value <= Integer.MIN_VALUE / right_value);
        }
    }

    @Override
    public int eval(int x, int y) {
        if (!save_multiply(x, y)) {
            throw new TooBigIntegerException("Overflow for mul!");
        }
        return x * y;
    }
}
