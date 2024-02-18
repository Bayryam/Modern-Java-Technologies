package bg.sofia.uni.fmi.mjt.crypto.exception;

public class CipherException extends Exception {
    public CipherException(String message) {
        super(message);
    }

    public CipherException(String message, Throwable cause) {
        super(message, cause);
    }
}
