package expression.exceptions;

import expression.BinaryOperation;
import expression.Parent;

import java.math.BigDecimal;

public class CheckedPow extends BinaryOperation {
    public CheckedPow(Parent left, Parent right) {
        super(left, right);
    }

    public int pow(int x, int y) {
        int ans = 1;
        if (x == 0) return 0;
        if (x == 1) return 1;
        if (x == -1) return y % 2 == 0 ? 1 : -1;
        for (int i = 0; i < y; i++) {
            if (CheckedMultiply.save_multiply(ans, x)) {
                ans *= x;
            } else {
                throw new TooBigIntegerException("Too big pow!");
            }
        }
        return ans;
    }

    @Override
    public BigDecimal eval(BigDecimal x, BigDecimal y) {
        throw new IllegalArgumentException();
    }

    @Override
    public int eval(int x, int y) {
        if (x == 0 && y <= 0 || x != 0 && y < 0) {
            throw new NotAppropriateNumberException("Not supported numbers for pow!");
        }
        return pow(x, y);
    }

    @Override
    public String getBinaryOperand() {
        return "**";
    }

    @Override
    public int getPriority() {
        return 3;
    }

    @Override
    public boolean isSideDependence() {
        return false;
    }

    @Override
    public boolean mustWrapRight() {
        return true;
    }
}
