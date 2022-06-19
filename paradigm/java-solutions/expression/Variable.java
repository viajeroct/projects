package expression;

import expression.types.Type;

public class Variable<T> extends Parent<T> {
    private final String x;

    public Variable(String x) {
        this.x = x;
    }

    @Override
    public String toString() {
        return x;
    }

    @Override
    public void toStringFast(StringBuilder res) {
        res.append(x);
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int hashCode() {
        return x.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable<?> cur) {
            return this.x.equals(cur.x);
        }
        return false;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (this.x) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBinaryOperand() {
        return "";
    }

    @Override
    public int getPriority() {
        return 4;
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
    public T evaluate(T x, T y, T z, Type<T> type) {
        switch (this.x) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
        }
        throw new UnsupportedOperationException();
    }
}
