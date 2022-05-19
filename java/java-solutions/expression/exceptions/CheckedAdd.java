package expression.exceptions;

import expression.Add;
import expression.Parent;

public class CheckedAdd extends Add {
    public CheckedAdd(Parent left, Parent right) {
        super(left, right);
    }

    @Override
    public int eval(int x, int y) {
        if (y < 0) {
            if (x >= Integer.MIN_VALUE - y) {
                return x + y;
            }
        } else if (x <= Integer.MAX_VALUE - y) {
            return x + y;
        }
        throw new TooBigIntegerException("Overflow add error!");
    }
}
