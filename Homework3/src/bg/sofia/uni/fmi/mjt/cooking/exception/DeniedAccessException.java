package bg.sofia.uni.fmi.mjt.cooking.exception;

public class DeniedAccessException extends Exception {
    public DeniedAccessException(String message) {
        super(message);
    }

    public DeniedAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
