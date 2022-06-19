package expression.types;

public class SimpleLongType implements Type<Long> {
    @Override
    public Long add(Long x, Long y) {
        return x + y;
    }

    @Override
    public Long subtract(Long x, Long y) {
        return x - y;
    }

    @Override
    public Long multiply(Long x, Long y) {
        return x * y;
    }

    @Override
    public Long divide(Long x, Long y) {
        return x / y;
    }

    @Override
    public Long parse(String data) {
        return Long.parseLong(data);
    }

    @Override
    public Long negate(Long x) {
        return -x;
    }

    @Override
    public Long castToT(int x) {
        return (long) x;
    }

    @Override
    public Long count(Long x) {
        return (long) Long.bitCount(x);
    }

    @Override
    public Long min(Long x, Long y) {
        return Math.min(x, y);
    }

    @Override
    public Long max(Long x, Long y) {
        return Math.max(x, y);
    }
}
