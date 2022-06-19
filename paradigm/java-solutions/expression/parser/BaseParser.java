package expression.parser;

import expression.exceptions.NonAlphabeticSymbolException;
import expression.exceptions.WrongNumberFormatException;

import java.util.Arrays;

public class BaseParser {
    static private final char END = '\0';
    public char ch = 0xffff;
    private CharSource source;
    private int errorPosition = 0;
    public static final String[] vars = {"x", "y", "z"};
    private static final int contextSize = 4;
    private static int curContextPos = 0;
    private static char[] errorContext;

    protected BaseParser() {
    }

    protected String getErrorContext() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < contextSize; i++) {
            res.append(errorContext[(i + curContextPos) % contextSize]);
        }
        return res.toString();
    }

    protected void init(final String expr) {
        this.source = new StringCharSource(expr);
        this.errorPosition = -1;
        errorContext = new char[contextSize];
        Arrays.fill(errorContext, END);
        curContextPos = 0;
        take();
    }

    protected void takeBig(final char... dt) {
        for (char it : dt) {
            take(it);
        }
    }

    protected int checkString(String s) {
        char start_ch = ch;
        for (int i = 0; i < s.length(); i++) {
            if (!test(s.charAt(i))) {
                source.prev(i);
                ch = start_ch;
                return i;
            }
            take(s.charAt(i));
        }
        return -1;
    }

    protected char predictNext() {
        return source.predictNext();
    }

    protected int convert(String number) {
        return Integer.parseInt(number);
    }

    protected String getNumber(final boolean negate) {
        StringBuilder ans = new StringBuilder();
        if (negate) {
            ans.append("-");
        }
        if (!Character.isDigit(ch)) {
            throw new WrongNumberFormatException("Expected number but found " + ch + ".");
        }
        while (Character.isDigit(ch)) {
            ans.append(take());
        }
        return ans.toString();
    }

    protected void skipWhitespace() {
        while (Character.isWhitespace(ch)) {
            take();
        }
        if (isNotEof() &&
                !equals('(', 'x', 'y', 'z', '-', '*', '-', '/', '+', ')', '<', '>', 'l', 't',
                        'a', 'b', 's', 'c', 'o', 'u', 'n', 't', 'm', 'i', 'n', 'm', 'a', 'x') &&
                !Character.isDigit(ch)) {
            throw new NonAlphabeticSymbolException("You should use " +
                    "'(', '-', '*', '-', '/', '+', ')', '<', '>', 'l0', 't0', 'abs', '0' - '9' " +
                    Arrays.toString(vars) + " but you use: " + ch + ". (pos=" + errorPosition + ", context=" +
                    getErrorContext() + ")");
        }
    }

    protected boolean equals(final char... symbols) {
        for (char it : symbols) {
            if (test(it)) {
                return true;
            }
        }
        return false;
    }

    protected char take() {
        final char result = ch;
        if (source.hasNext()) {
            errorPosition++;
            errorContext[curContextPos++] = result;
            curContextPos %= contextSize;
        }
        ch = source.hasNext() ? source.next() : END;
        return result;
    }

    protected boolean test(final char expected) {
        return ch == expected;
    }

    protected boolean take(final char expected) {
        if (test(expected)) {
            take();
            return true;
        }
        return false;
    }

    protected int getErrorPosition() {
        return errorPosition > 0 ? errorPosition - 1 : 0;
    }

    protected boolean isNotEof() {
        return !take(END);
    }
}
