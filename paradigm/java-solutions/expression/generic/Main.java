package expression.generic;

import expression.Parent;
import expression.exceptions.ExpressionParser;
import expression.types.Type;

import static expression.generic.GenericTabulator.MODES;

public class Main {
    private static <T> void calculate(Type<T> type, String expression) throws Exception {
        ExpressionParser<T> parser = new ExpressionParser<>();
        Parent<T> answer = parser.parse(expression);
        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -2; k <= 2; k++) {
                    T current;
                    try {
                        current = answer.evaluate(type.castToT(i), type.castToT(j), type.castToT(k), type);
                    } catch (Exception e) {
                        current = null;
                    }
                    System.out.printf("[x=%d, y=%d, z=%d]:\n", i, j, k);
                    System.out.println("\t[res=" + current + "] (expr=" + expression + ")");
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Too few arguments!");
        }
        String mode = args[0].substring(1);
        StringBuilder res = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            res.append(args[i]);
        }
        String expression = res.toString();

        try {
            calculate(MODES.get(mode), expression);
        } catch (Exception e) {
            System.err.println("Smth went wrong: " + e.getMessage());
        }
    }
}
