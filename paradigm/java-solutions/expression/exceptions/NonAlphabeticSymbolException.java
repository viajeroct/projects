package expression.exceptions;

public class NonAlphabeticSymbolException extends UnexpectedSymbolException {
    public NonAlphabeticSymbolException(String message) {
        super(message);
    }
}
