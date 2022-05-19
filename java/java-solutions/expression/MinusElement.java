package expression;

import java.math.BigDecimal;

public class MinusElement extends UnaryOperation {
    public MinusElement(Parent innerElement) {
        super(innerElement);
    }

    @Override
    public int eval(int x) {
        return -x;
    }

    @Override
    public BigDecimal evaluate(BigDecimal x) {
        return innerElement.evaluate(x).multiply(BigDecimal.valueOf(-1));
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
