package bg.sofia.uni.fmi.mjt.crypto.cache;

import bg.sofia.uni.fmi.mjt.crypto.dto.Equity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EquitiesCacheTest {
    private final EquitiesCache equitiesCache = new EquitiesCache();
    private final Equity equity1 = new Equity("BTC", "Bitcoin", 1, BigDecimal.valueOf(1000));
    private final Equity equity2 = new Equity("ETH", "Ethereum", 1, BigDecimal.valueOf(1000));
    private final Equity equity3 = new Equity("ADA", "Cardano", 1, BigDecimal.valueOf(1000));

    @Test
    void testUpdateCacheWithNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> equitiesCache.updateCache(null),
            "IllegalArgumentException should be thrown when trying to update the cache with null!");
    }

    @Test
    void testUpdateCacheEmptyCacheValidCollection() {
        Collection<Equity> equities = new ArrayList<>();
        equities.add(equity1);
        equities.add(equity2);
        equitiesCache.updateCache(equities);
        assertEquals(2, equitiesCache.getCachedValues().size());
        assertEquals("BTC", equitiesCache.getCachedValues().stream().toList().getFirst().equityId());
        assertEquals("ETH", equitiesCache.getCachedValues().stream().skip(1).toList().getFirst().equityId());
    }

    @Test
    void testUpdateCacheOldValuesCacheValidCollection() {
        Collection<Equity> oldEquities = new ArrayList<>();
        oldEquities.add(equity3);
        equitiesCache.updateCache(oldEquities);
        Collection<Equity> newEquities = new ArrayList<>();
        newEquities.add(equity1);
        newEquities.add(equity2);
        equitiesCache.updateCache(newEquities);
        assertFalse(equitiesCache.getCachedValues().contains(equity3));
    }

}
