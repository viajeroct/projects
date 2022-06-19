package expression.types;

public class TenType implements Type<Integer> {
    private int f(int t) {
        return t - t % 10;
    }

    @Override
    public Integer add(Integer x, Integer y) {
        return f(f(x) + f(y));
    }

    @Override
    public Integer subtract(Integer x, Integer y) {
        return f(f(x) - f(y));
    }

    @Override
    public Integer multiply(Integer x, Integer y) {
        return f(f(x) * f(y));
    }

    @Override
    public Integer divide(Integer x, Integer y) {
        return f(f(x) / f(y));
    }

    @Override
    public Integer parse(String data) {
        return Integer.parseInt(data);
    }

    @Override
    public Integer negate(Integer x) {
        return f(-f(x));
    }

    @Override
    public Integer castToT(int x) {
        return f(x);
    }

    @Override
    public Integer count(Integer x) {
        return f(Integer.bitCount(f(x)));
    }

    @Override
    public Integer min(Integer x, Integer y) {
        return f(Math.min(f(x), f(y)));
    }

    @Override
    public Integer max(Integer x, Integer y) {
        return f(Math.max(f(x), f(y)));
    }
}
