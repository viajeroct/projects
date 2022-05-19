package expression;

public abstract class Parent extends StringMethodsInterface
        implements Expression, TripleExpression, BigDecimalExpression, BinaryParent {
    public abstract int getPriority();

    public abstract boolean isSideDependence();

    public abstract boolean mustWrapRight();
}
