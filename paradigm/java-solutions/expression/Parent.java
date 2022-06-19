package expression;

import expression.types.Type;

public abstract class Parent<T> extends StringMethodsInterface implements Expression, TripleExpression, BinaryParent {
    public abstract int getPriority();

    public abstract boolean isSideDependence();

    public abstract boolean mustWrapRight();

    public abstract T evaluate(T x, T y, T z, Type<T> type);
}
