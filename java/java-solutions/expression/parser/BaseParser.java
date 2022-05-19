package expression.parser;

import expression.exceptions.NonAlphabeticSymbolException;
import expression.exceptions.WrongNumberFormatException;

public class BaseParser {
    static private final char END = '\0';
    public char ch = 0xffff;
    private CharSource source;

    protected BaseParser() {
    }

    protected void init(final String expr) {
        this.source = new StringCharSource(expr);
        take();
    }

    protected void takeBig(final char... dt) {
        for (char it : dt) {
            take(it);
        }
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
                        'a', 'b', 's') &&
                !Character.isDigit(ch)) {
            throw new NonAlphabeticSymbolException("You should use " +
                    "'(', 'x', 'y', 'z', '-', '*', '-', '/', '+', ')', '<', '>', 'l', 't', 'abs', '0' - '9'" +
                    " but you use: " + ch);
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

    protected boolean isNotEof() {
        return !take(END);
    }
}
