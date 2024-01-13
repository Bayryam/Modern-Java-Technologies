package bg.sofia.uni.fmi.order.server.tshirt;

import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TShirtTest {
    @Test
    void testToString() {
        TShirt tshirt = new TShirt(Size.S, Color.BLACK);
        assertEquals("{\"size\":\"S\", \"color\":\"BLACK\"}", tshirt.toString());
    }
}
