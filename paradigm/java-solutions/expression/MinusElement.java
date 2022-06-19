package expression;

import expression.types.Type;

public class MinusElement<T> extends UnaryOperation<T> {
    public MinusElement(Parent<T> innerElement) {
        super(innerElement);
    }

    @Override
    public T evaluate(T x, Type<T> type) {
        return type.negate(x);
    }

    @Override
    public int eval(int x) {
        return -x;
    }

    @Override
    public String getPrefix() {
        return "-";
    }

    @Override
    public int getPriority() {
        return 4;
    }
}
