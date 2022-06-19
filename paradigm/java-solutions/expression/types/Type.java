package expression.types;

public interface Type<T> {
    T add(T x, T y);

    T subtract(T x, T y);

    T multiply(T x, T y);

    T divide(T x, T y);

    T parse(String data);

    T negate(T x);

    T castToT(int x);

    T count(T x);

    T min(T x, T y);

    T max(T x, T y);
}
