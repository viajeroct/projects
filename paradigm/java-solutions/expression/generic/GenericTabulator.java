package expression.generic;

import expression.Parent;
import expression.exceptions.ExpressionParser;
import expression.types.*;

import java.util.Map;

public class GenericTabulator implements Tabulator {
    public final static Map<String, Type<?>> MODES = Map.of(
            "i", new IntegerType(),
            "d", new DoubleType(),
            "bi", new BigIntegerType(),
            "u", new SimpleIntegerType(),
            "l", new SimpleLongType(),
            "t", new TenType());

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        return tabulateImpl(MODES.get(mode), expression, x1, x2, y1, y2, z1, z2);
    }

    private <T> Object[][][] tabulateImpl(Type<T> type, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        ExpressionParser<T> parser = new ExpressionParser<>();
        Parent<T> parsed = parser.parse(expression);
        Object[][][] answer = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int i = x1; i <= x2; i++) {
            for (int j = y1; j <= y2; j++) {
                for (int k = z1; k <= z2; k++) {
                    try {
                        answer[i - x1][j - y1][k - z1] = parsed.evaluate(type.castToT(i), type.castToT(j), type.castToT(k), type);
                    } catch (Exception e) {
                        answer[i - x1][j - y1][k - z1] = null;
                    }
                }
            }
        }
        return answer;
    }
}
