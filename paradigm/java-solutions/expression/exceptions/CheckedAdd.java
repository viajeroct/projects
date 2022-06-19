package expression.exceptions;

import expression.Add;
import expression.Parent;

public class CheckedAdd<T> extends Add<T> {
    public CheckedAdd(Parent<T> left, Parent<T> right) {
        super(left, right);
    }

    public static int save_add(int x, int y) {
        if (y < 0) {
            if (x >= Integer.MIN_VALUE - y) {
                return x + y;
            }
        } else if (x <= Integer.MAX_VALUE - y) {
            return x + y;
        }
        throw new TooBigIntegerException("Overflow add error!");
    }

    @Override
    public int eval(int x, int y) {
        return save_add(x, y);
    }
}
