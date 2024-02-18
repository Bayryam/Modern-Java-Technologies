package bg.sofia.uni.fmi.mjt.crypto.exception;

public class DuplicateUsernameException extends Exception {
    public DuplicateUsernameException(String message) {
        super(message);
    }

    public DuplicateUsernameException(String message, Throwable cause) {
        super(message, cause);
    }
}
