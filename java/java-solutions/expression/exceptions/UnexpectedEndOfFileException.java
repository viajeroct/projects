package expression.exceptions;

public class UnexpectedEndOfFileException extends SemanticException {
    public UnexpectedEndOfFileException(String message) {
        super(message);
    }
}
