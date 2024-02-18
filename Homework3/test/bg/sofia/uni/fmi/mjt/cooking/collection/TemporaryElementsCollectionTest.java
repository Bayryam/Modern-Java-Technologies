package bg.sofia.uni.fmi.mjt.cooking.collection;

import org.junit.jupiter.api.Test;

import static java.lang.Thread.sleep;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TemporaryElementsCollectionTest {
    private final TemporaryElementsCollection<String, String> col =
        new TemporaryElementsCollection<>(1);

    @Test
    void testGetWithNotExistingElement() {
        assertNull(col.get("not_existing_key"), "Element with such key was not added!");
    }

    @Test
    void testGetExistingElement() {
        String expected = "wanted_value";
        col.put("existing_key", expected);
        assertEquals(expected, col.get("existing_key"),
            String.format("Element with such key should be in the collection with value: %s!", expected));
    }

    @Test
    void testGetExpiredElement() throws InterruptedException {
        col.put("expiring_key", "expiring_value");
        final int timeForSleep = 2000;
        sleep(timeForSleep);
        assertNull(col.get("expiring_key"), "Element with key: expiring_key already expired!");
    }

    @Test
    void testPutElement() {
        String expected = "wanted_value";
        col.put("existing_key", expected);
        assertEquals(expected, col.get("existing_key"),
            "Element with key: existing_key and value: wanted_value should be in the collection!");
    }
}
