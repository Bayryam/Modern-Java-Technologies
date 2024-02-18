package bg.sofia.uni.fmi.mjt.crypto.exception;

public class InternalServerException extends Exception {
    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
