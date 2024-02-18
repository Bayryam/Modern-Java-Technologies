package bg.sofia.uni.fmi.mjt.crypto.cache;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EquitiesCache implements Cache<Equity> {
    private final Set<Equity> equities;

    public EquitiesCache() {
        equities = new HashSet<>();
    }

    @Override
    public Collection<Equity> getCachedValues() {
        return equities;
    }

    @Override
    public void updateCache(Collection<Equity> newValues) {
        if (newValues == null) {
            throw new IllegalArgumentException("The new values cannot be null!");
        }
        equities.clear();
        equities.addAll(newValues);
    }
}
