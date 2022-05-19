package expression.parser;

import expression.*;

public class ExpressionParser extends BaseParser implements Parser {
    @Override
    public Parent parse(final String expression) {
        init(expression);
        return mainParser();
    }

    private Parent mainParser() {
        Parent answer = parseAddSubtract();
        skipWhitespace();
        do {
            if (test('<')) {
                takeBig('<', '<');
                answer = new Shift(answer, parseAddSubtract(), "<<");
            } else if (test('>')) {
                takeBig('>', '>');
                if (test('>')) {
                    take('>');
                    answer = new Shift(answer, parseAddSubtract(), ">>>");
                } else {
                    answer = new Shift(answer, parseAddSubtract(), ">>");
                }
            }
            skipWhitespace();
        } while (equals('<', '>'));
        return answer;
    }

    private Parent parseAddSubtract() {
        Parent answer = parseMultiplyDivide();
        skipWhitespace();
        do {
            if (take('+')) {
                answer = new Add(answer, parseMultiplyDivide());
            } else if (take('-')) {
                answer = new Subtract(answer, parseMultiplyDivide());
            }
            skipWhitespace();
        } while (equals('+', '-'));
        return answer;
    }

    private Parent parseMultiplyDivide() {
        Parent answer = parseBasement();
        skipWhitespace();
        do {
            if (test('*') && predictNext() != '*') {
                take();
                answer = new Multiply(answer, parseBasement());
            } else if (take('/')) {
                answer = new Divide(answer, parseBasement());
            }
            skipWhitespace();
        } while (equals('*', '/'));
        return answer;
    }

    private Parent parseBasement() {
        skipWhitespace();
        while (isNotEof()) {
            if (equals('x', 'y', 'z')) {
                return new Variable(String.valueOf(take()));
            } else if (Character.isDigit(ch)) {
                return new Const(Integer.parseInt(getNumber(false)));
            } else if (test('-')) {
                take();
                if (Character.isDigit(ch)) {
                    return new Const(Integer.parseInt(getNumber(true)));
                } else {
                    return new MinusElement(parseBasement());
                }
            } else if (take('(')) {
                Parent innerParentheses = mainParser();
                take(')');
                return innerParentheses;
            } else if (equals('l', 't')) {
                if (test('l')) {
                    takeBig('l', '0');
                    return new UnaryZeroes(parseBasement(), true);
                } else if (test('t')) {
                    takeBig('t', '0');
                    return new UnaryZeroes(parseBasement(), false);
                }
            }
        }
        throw new IllegalArgumentException();
    }
}
