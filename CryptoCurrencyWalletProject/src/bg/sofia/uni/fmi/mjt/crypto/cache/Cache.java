package bg.sofia.uni.fmi.mjt.crypto.cache;

import java.util.Collection;

public interface Cache<T extends Cacheable> {
    /**
     * Retrieves all cached values.
     *
     * @return a collection of all cached values.
     */
    Collection<T> getCachedValues();

    /**
     * Updates the cache with new values.
     *
     * @param newValues the new values to be added to the cache.
     */
    void updateCache(Collection<T> newValues);
}
