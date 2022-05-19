package expression;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class BinaryOperation extends Parent {
    public Parent left, right;

    public BinaryOperation(Parent left, Parent right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public BigDecimal evaluate(BigDecimal x) {
        return eval(left.evaluate(x), right.evaluate(x));
    }

    @Override
    public int evaluate(int x) {
        return eval(left.evaluate(x), right.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return eval(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    public abstract BigDecimal eval(BigDecimal x, BigDecimal y);

    public abstract int eval(int x, int y);

    @Override
    public int hashCode() {
        return Objects.hash(left, right, getBinaryOperand());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BinaryOperation) {
            BinaryOperation other = (BinaryOperation) obj;
            return this.left.equals(other.left) && this.right.equals(other.right) &&
                    this.getBinaryOperand().equals(other.getBinaryOperand());
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        toStringFast(res);
        return res.toString();
    }

    @Override
    public void toStringFast(StringBuilder res) {
        res.append("(");
        left.toStringFast(res);
        res.append(" ").append(getBinaryOperand()).append(" ");
        right.toStringFast(res);
        res.append(")");
    }

    @Override
    public void toMiniStringInner(StringBuilder res) {
        boolean needLeftParentheses = false;
        boolean needRightParentheses = false;
        if (this.getPriority() > left.getPriority()) {
            needLeftParentheses = true;
        }
        if (this.getPriority() > right.getPriority()) {
            needRightParentheses = true;
        } else {
            if (right.getPriority() <= this.getPriority()) {
                if (right.mustWrapRight()) {
                    needRightParentheses = true;
                } else {
                    if (this.isSideDependence()) {
                        needRightParentheses = true;
                    }
                }
            }
        }
        if (needLeftParentheses) res.append("(");
        left.toMiniStringInner(res);
        if (needLeftParentheses) res.append(")");

        res.append(" ").append(getBinaryOperand()).append(" ");

        if (needRightParentheses) res.append("(");
        right.toMiniStringInner(res);
        if (needRightParentheses) res.append(")");
    }

    @Override
    public String toMiniString() {
        StringBuilder res = new StringBuilder();
        toMiniStringInner(res);
        return res.toString();
    }
}
