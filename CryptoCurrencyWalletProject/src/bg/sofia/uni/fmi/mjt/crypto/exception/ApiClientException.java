package bg.sofia.uni.fmi.mjt.crypto.exception;

public class ApiClientException extends Exception {
    public ApiClientException(String message) {
        super(message);
    }

    public ApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
