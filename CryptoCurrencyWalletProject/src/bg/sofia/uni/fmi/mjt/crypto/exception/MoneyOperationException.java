package bg.sofia.uni.fmi.mjt.crypto.exception;

public class MoneyOperationException extends Exception {
    public MoneyOperationException(String message) {
        super(message);
    }

    public MoneyOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
