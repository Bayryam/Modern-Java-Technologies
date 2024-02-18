package bg.sofia.uni.fmi.mjt.cooking.collection;

public interface KeyValuePairCollection<K, V> {
    V get(K key);

    void put(K key, V value);
}
