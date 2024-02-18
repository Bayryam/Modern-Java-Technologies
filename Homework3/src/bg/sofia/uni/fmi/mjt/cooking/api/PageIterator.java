package bg.sofia.uni.fmi.mjt.cooking.api;

import bg.sofia.uni.fmi.mjt.cooking.exception.DeniedAccessException;
import bg.sofia.uni.fmi.mjt.cooking.exception.RecipesNotRetrievedException;

public interface PageIterator<T> {
    boolean hasNext();

    T next() throws DeniedAccessException, RecipesNotRetrievedException;
}