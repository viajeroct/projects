package expression;

import expression.types.Type;

public class Count<T> extends UnaryOperation<T> {
    public Count(Parent<T> innerElement) {
        super(innerElement);
    }

    @Override
    public int getPriority() {
        return 4;
    }

    @Override
    public T evaluate(T x, Type<T> type) {
        return type.count(x);
    }

    @Override
    public int eval(int x) {
        return Integer.bitCount(x);
    }

    @Override
    public String getPrefix() {
        return "count";
    }
}
