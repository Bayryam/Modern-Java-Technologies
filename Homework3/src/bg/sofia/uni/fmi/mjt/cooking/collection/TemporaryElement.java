package bg.sofia.uni.fmi.mjt.cooking.collection;

import java.time.Duration;
import java.time.LocalDateTime;

public class TemporaryElement<V> {
    private final V elementValue;
    private final LocalDateTime timeOfCreation;

    public TemporaryElement(V object) {
        this.elementValue = object;
        this.timeOfCreation = LocalDateTime.now();
    }

    public V getElementValue() {
        return elementValue;
    }

    public long timeBeing() {
        LocalDateTime currentMoment = LocalDateTime.now();
        return Duration.between(timeOfCreation, currentMoment).getSeconds();
    }
}
