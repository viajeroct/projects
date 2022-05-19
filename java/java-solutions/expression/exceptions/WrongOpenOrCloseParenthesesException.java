package expression.exceptions;

public class WrongOpenOrCloseParenthesesException extends SemanticException {
    public WrongOpenOrCloseParenthesesException(String message) {
        super(message);
    }
}
