package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IoTDeviceBaseTest {
    IoTDevice bulb = new RgbBulb("bulb", 7, LocalDateTime.now());

    @Test
    void testGetId() {
        assertEquals("BLB-bulb-0", bulb.getId());
    }

    @Test
    void testGetName() {
        assertEquals("bulb", bulb.getName());
    }

    @Test
    void testGetType() {
        assertEquals(DeviceType.BULB, bulb.getType());
    }
}
