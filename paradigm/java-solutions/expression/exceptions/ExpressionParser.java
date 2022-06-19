package expression.exceptions;

import expression.*;
import expression.parser.BaseParser;

import java.util.Arrays;

public class ExpressionParser<T> extends BaseParser implements TripleParser {
    @Override
    public Parent<T> parse(final String expression) throws Exception {
        init(expression);
        Parent<T> answer = mainParser();
        if (isNotEof()) {
            throw new UnexpectedEndOfFileException(String.format("Incorrect end of file. Not expected EOF. (pos=%d, context=%s)", getErrorPosition(), getErrorContext()));
        }
        return answer;
    }

    private Parent<T> mainParser() throws Exception {
        Parent<T> answer = shiftParser();
        while (true) {
            skipWhitespace();
            if (checkString("min") == -1) {
                answer = new Min<>(answer, shiftParser());
            } else if (checkString("max") == -1) {
                answer = new Max<>(answer, shiftParser());
            } else {
                return answer;
            }
        }
    }

    private Parent<T> shiftParser() throws Exception {
        Parent<T> answer = parseAddSubtract();
        while (true) {
            skipWhitespace();
            if (test('<')) {
                takeBig('<', '<');
                answer = new Shift<>(answer, parseAddSubtract(), "<<");
            } else if (test('>')) {
                takeBig('>', '>');
                if (test('>')) {
                    take('>');
                    answer = new Shift<>(answer, parseAddSubtract(), ">>>");
                } else {
                    answer = new Shift<>(answer, parseAddSubtract(), ">>");
                }
            } else {
                return answer;
            }
        }
    }

    private Parent<T> parseAddSubtract() throws Exception {
        Parent<T> answer = parseMultiplyDivide();
        while (true) {
            skipWhitespace();
            if (take('+')) {
                answer = new CheckedAdd<>(answer, parseMultiplyDivide());
            } else if (take('-')) {
                answer = new CheckedSubtract<>(answer, parseMultiplyDivide());
            } else {
                return answer;
            }
        }
    }

    private Parent<T> parseMultiplyDivide() throws Exception {
        Parent<T> answer = parsePowLog();
        while (true) {
            skipWhitespace();
            if (test('*') && predictNext() != '*') {
                take();
                answer = new CheckedMultiply<>(answer, parsePowLog());
            } else if (test('/') && predictNext() != '/') {
                take();
                answer = new CheckedDivide<>(answer, parsePowLog());
            } else {
                return answer;
            }
        }
    }

    private Parent<T> parsePowLog() throws Exception {
        Parent<T> answer = parseBasement();
        while (true) {
            skipWhitespace();
            if (test('/') && predictNext() == '/') {
                takeBig('/', '/');
                skipWhitespace();
                answer = new CheckedLog<>(answer, parseBasement());
            } else if (test('*') && predictNext() == '*') {
                takeBig('*', '*');
                skipWhitespace();
                answer = new CheckedPow<>(answer, parseBasement());
            } else {
                return answer;
            }
        }
    }

    private Parent<T> parseBasement() throws Exception {
        skipWhitespace();
        while (isNotEof()) {
            if (equals('x', 'y', 'z')) {
                return new Variable<>(String.valueOf(take()));
            } else if (test('c')) {
                takeBig('c', 'o', 'u', 'n', 't');
                return new Count<>(parseBasement());
            } else if (Character.isDigit(ch)) {
                return new Const<>(convert(getNumber(false)));
            } else if (test('-')) {
                take();
                if (Character.isDigit(ch)) {
                    return new Const<>(convert(getNumber(true)));
                } else {
                    return new CheckedNegate<>(parseBasement());
                }
            } else if (take('(')) {
                Parent<T> innerParentheses = mainParser();
                if (!test(')')) {
                    throw new WrongOpenOrCloseParenthesesException(String.format("In this place expecting closing parentheses, but found: \"%c\". (pos=%d, context=%s)", ch, getErrorPosition(), getErrorContext()));
                }
                take();
                return innerParentheses;
            } else if (equals('l', 't')) {
                if (test('l')) {
                    takeBig('l', '0');
                    return new UnaryZeroes<>(parseBasement(), true);
                } else if (test('t')) {
                    takeBig('t', '0');
                    return new UnaryZeroes<>(parseBasement(), false);
                }
            } else if (test('a')) {
                take();
                if (test('b')) {
                    take();
                    if (test('s')) {
                        take();
                        if (Character.isLetter(ch) || Character.isDigit(ch)) {
                            throw new WrongAbsFormatException(String.format("Wrong usage of function \"abs\": %s. (pos=%d, context=%s)", String.format("abs%c", ch), getErrorPosition(), getErrorContext()));
                        }
                        return new CheckedAbs<>(parseBasement());
                    } else {
                        throw new WrongAbsFormatException(String.format("Such command is not supported: %s. But there is similar command \"abs\". (pos=%d, context=%s)", String.format("ab%c", take()), getErrorPosition(), getErrorContext()));
                    }
                } else {
                    throw new WrongAbsFormatException(String.format("Such command is not supported: %s. But there is similar command \"abs\". (pos=%d, context=%s)", String.format("a%c", take()), getErrorPosition(), getErrorContext()));
                }
            } else {
                if (equals('*', '/', '-', '+', '<', '>')) {
                    String command = String.valueOf(take());
                    if (command.equals(">")) {
                        command += ">";
                    } else if (command.equals("<")) {
                        command += "<";
                    }
                    throw new AbsenceArgumentOfBinaryOperationException(String.format("Missing of argument in binary operation" + " '" + command + "'. (pos=%d, context=%s)", getErrorPosition(), getErrorContext()));
                } else if (test(')')) {
                    throw new UnexpectedClosingParentheses(String.format("Unexpected end of brackets. (pos=%d, context=%s)", getErrorPosition(), getErrorContext()));
                } else {
                    throw new SemanticException(String.format("On this level expected variable %s or " + "number or opening parentheses or unary command('l0', 't0') or" + " abc command, but found " + take() + ". (pos=%d, context=%s)", Arrays.toString(vars), getErrorPosition(), getErrorContext()));
                }
            }
        }
        throw new SemanticException(String.format("Absence of argument. On this level expected variable %s or " + "number or opening parentheses or unary command('l0', 't0') or" + " abc command, but found " + take() + ". (pos=%d, context=%s)", Arrays.toString(vars), getErrorPosition(), getErrorContext()));
    }
}
