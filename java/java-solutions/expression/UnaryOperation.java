package expression;

import java.math.BigDecimal;

public abstract class UnaryOperation extends Parent {
    protected final Parent innerElement;

    public UnaryOperation(Parent innerElement) {
        this.innerElement = innerElement;
    }

    public abstract int eval(int x);

    @Override
    public int evaluate(int x, int y, int z) {
        return eval(innerElement.evaluate(x, y, z));
    }

    @Override
    public boolean isSideDependence() {
        return false;
    }

    @Override
    public boolean mustWrapRight() {
        return false;
    }

    @Override
    public BigDecimal evaluate(BigDecimal x) {
        throw new IllegalArgumentException();
    }

    @Override
    public String getBinaryOperand() {
        throw new IllegalArgumentException();
    }

    public abstract String getPrefix();

    @Override
    public void toMiniStringInner(StringBuilder res) {
        boolean needParentheses = innerElement.getPriority() <= 3;
        res.append(getPrefix());
        res.append(needParentheses ? "(" : " ");
        innerElement.toMiniStringInner(res);
        if (needParentheses) res.append(")");
    }

    @Override
    public String toString() {
        return getPrefix() + String.format("(%s)", innerElement.toString());
    }

    @Override
    public int evaluate(int x) {
        return eval(innerElement.evaluate(x));
    }

    @Override
    public void toStringFast(StringBuilder res) {
        res.append(toString());
    }

    @Override
    public String toMiniString() {
        StringBuilder res = new StringBuilder();
        toMiniStringInner(res);
        return res.toString();
    }
}
