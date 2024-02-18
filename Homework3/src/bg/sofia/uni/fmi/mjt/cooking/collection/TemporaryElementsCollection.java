package bg.sofia.uni.fmi.mjt.cooking.collection;

import java.util.HashMap;
import java.util.Map;

public class TemporaryElementsCollection<K, V> implements KeyValuePairCollection<K, V> {
    private final long seconds;
    private final Map<K, TemporaryElement<V>> storage;

    public TemporaryElementsCollection(long seconds) {
        this.seconds = seconds;
        this.storage = new HashMap<>();
    }

    @Override
    public V get(K key) {
        cleanupExpiredElements();
        TemporaryElement<V> temporaryElement = storage.get(key);
        if (temporaryElement == null) {
            return null;
        }

        return temporaryElement.getElementValue();
    }

    @Override
    public void put(K key, V value) {
        cleanupExpiredElements();
        storage.put(key, new TemporaryElement<>(value));
    }

    private void cleanupExpiredElements() {
        storage.entrySet().removeIf(entry -> entry.getValue().timeBeing() > seconds);
    }
}
