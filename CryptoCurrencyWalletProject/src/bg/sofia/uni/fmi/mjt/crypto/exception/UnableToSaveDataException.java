package bg.sofia.uni.fmi.mjt.crypto.exception;

public class UnableToSaveDataException extends Exception {
    public UnableToSaveDataException(String message) {
        super(message);
    }

    public UnableToSaveDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
