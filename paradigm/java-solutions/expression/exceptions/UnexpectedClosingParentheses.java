package expression.exceptions;

public class UnexpectedClosingParentheses extends WrongOpenOrCloseParenthesesException {
    public UnexpectedClosingParentheses(String message) {
        super(message);
    }
}
