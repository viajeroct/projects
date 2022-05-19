package expression.exceptions;

import expression.BinaryOperation;
import expression.Parent;

import java.math.BigDecimal;

public class CheckedLog extends BinaryOperation {
    public CheckedLog(Parent left, Parent right) {
        super(left, right);
    }

    public int log(int x, int base) {
        int ans = 0, cur = 1;
        while (CheckedMultiply.save_multiply(cur, base) && cur * base <= x) {
            cur *= base;
            ans++;
        }
        return ans;
    }

    @Override
    public BigDecimal eval(BigDecimal x, BigDecimal y) {
        throw new IllegalArgumentException();
    }

    @Override
    public int eval(int x, int y) {
        if (x <= 0 || y <= 0 || y == 1) {
            throw new NotAppropriateNumberException("Not supported numbers for log!");
        }
        return log(x, y);
    }

    @Override
    public String getBinaryOperand() {
        return "//";
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
