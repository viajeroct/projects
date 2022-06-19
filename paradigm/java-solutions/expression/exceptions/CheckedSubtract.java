package expression.exceptions;

import expression.Parent;
import expression.Subtract;

public class CheckedSubtract<T> extends Subtract<T> {
    public CheckedSubtract(Parent<T> left, Parent<T> right) {
        super(left, right);
    }

    public static int save_sub(int x, int y) {
        if (y >= 0) {
            if (x >= Integer.MIN_VALUE + y) {
                return x - y;
            }
        } else if (x <= Integer.MAX_VALUE + y) {
            return x - y;
        }
        throw new TooBigIntegerException("Overflow subtract error!");
    }

    @Override
    public int eval(int x, int y) {
        return save_sub(x, y);
    }
}
