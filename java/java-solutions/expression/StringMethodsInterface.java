package expression;

public abstract class StringMethodsInterface {
    public abstract void toStringFast(StringBuilder res);

    public void toMiniStringInner(StringBuilder res) {
        toStringFast(res);
    }
}
