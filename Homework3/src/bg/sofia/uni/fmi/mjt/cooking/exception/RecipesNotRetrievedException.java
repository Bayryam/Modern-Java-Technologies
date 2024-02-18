package bg.sofia.uni.fmi.mjt.cooking.exception;

public class RecipesNotRetrievedException extends Exception {
    public RecipesNotRetrievedException(String message) {
        super(message);
    }

    public RecipesNotRetrievedException(String message, Throwable cause) {
        super(message, cause);
    }
}
