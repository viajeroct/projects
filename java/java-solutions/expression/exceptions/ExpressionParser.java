package expression.exceptions;

import expression.*;
import expression.parser.BaseParser;

public class ExpressionParser extends BaseParser implements Parser {
    @Override
    public Parent parse(final String expression) {
        init(expression);
        Parent answer = mainParser();
        if (isNotEof()) {
            throw new UnexpectedEndOfFileException("Incorrect end of file. Not expected EOF.");
        }
        return answer;
    }

    private Parent mainParser() {
        Parent answer = parseAddSubtract();
        while (true) {
            skipWhitespace();
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
            } else {
                return answer;
            }
        }
    }

    private Parent parseAddSubtract() {
        Parent answer = parseMultiplyDivide();
        while (true) {
            skipWhitespace();
            if (take('+')) {
                answer = new CheckedAdd(answer, parseMultiplyDivide());
            } else if (take('-')) {
                answer = new CheckedSubtract(answer, parseMultiplyDivide());
            } else {
                return answer;
            }
        }
    }

    private Parent parseMultiplyDivide() {
        Parent answer = parsePowLog();
        while (true) {
            skipWhitespace();
            if (test('*') && predictNext() != '*') {
                take();
                answer = new CheckedMultiply(answer, parsePowLog());
            } else if (test('/') && predictNext() != '/') {
                take();
                answer = new CheckedDivide(answer, parsePowLog());
            } else {
                return answer;
            }
        }
    }

    private Parent parsePowLog() {
        Parent answer = parseBasement();
        while (true) {
            skipWhitespace();
            if (test('/') && predictNext() == '/') {
                takeBig('/', '/');
                skipWhitespace();
                answer = new CheckedLog(answer, parseBasement());
            } else if (test('*') && predictNext() == '*') {
                takeBig('*', '*');
                skipWhitespace();
                answer = new CheckedPow(answer, parseBasement());
            } else {
                return answer;
            }
        }
    }

    private Parent parseBasement() {
        skipWhitespace();
        while (isNotEof()) {
            if (equals('x', 'y', 'z')) {
                return new Variable(String.valueOf(take()));
            } else if (Character.isDigit(ch)) {
                return new Const(convert(getNumber(false)));
            } else if (test('-')) {
                take();
                if (Character.isDigit(ch)) {
                    return new Const(convert(getNumber(true)));
                } else {
                    return new CheckedNegate(parseBasement());
                }
            } else if (take('(')) {
                Parent innerParentheses = mainParser();
                if (!test(')')) {
                    throw new WrongOpenOrCloseParenthesesException(String.format(
                            "In this place expecting closing parentheses, but found: \"%c\".", ch));
                }
                take();
                return innerParentheses;
            } else if (equals('l', 't')) {
                if (test('l')) {
                    takeBig('l', '0');
                    return new UnaryZeroes(parseBasement(), true);
                } else if (test('t')) {
                    takeBig('t', '0');
                    return new UnaryZeroes(parseBasement(), false);
                }
            } else if (test('a')) {
                take();
                if (test('b')) {
                    take();
                    if (test('s')) {
                        take();
                        if (Character.isLetter(ch) || Character.isDigit(ch)) {
                            throw new WrongAbsFormatException(String.format(
                                    "Wrong usage of function \"abs\": %s.", String.format("abs%c", ch)));
                        }
                        return new CheckedAbs(parseBasement());
                    } else {
                        throw new WrongAbsFormatException(String.format(
                                "Such command is not supported: %s. But there is similar command \"abs\".",
                                String.format("ab%c", take())));
                    }
                } else {
                    throw new WrongAbsFormatException(String.format(
                            "Such command is not supported: %s. But there is similar command \"abs\".",
                            String.format("a%c", take())));
                }
            } else {
                if (equals('*', '/', '-', '+', '<', '>')) {
                    String command = String.valueOf(take());
                    if (command.equals(">")) {
                        command += ">";
                    } else if (command.equals("<")) {
                        command += "<";
                    }
                    throw new AbsenceArgumentOfBinaryOperationException("Missing of argument in binary operation" +
                            " '" + command + "'.");
                } else if (test(')')) {
                    throw new UnexpectedClosingParentheses("Unexpected end of brackets.");
                } else {
                    throw new SemanticException("On this level expected variable('x', 'y', 'z') or " +
                            "number or opening parentheses or unary command('l0', 't0') or" +
                            " abc command, but found " + take() + ".");
                }
            }
        }
        throw new SemanticException("Absence of argument. On this level expected variable('x', 'y', 'z') or " +
                "number or opening parentheses or unary command('l0', 't0') or" +
                " abc command, but found " + take() + ".");
    }
}
