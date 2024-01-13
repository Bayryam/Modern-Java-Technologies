package bg.sofia.uni.fmi.order.server.order;

import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {
    @Test
    void testToString() {
        Order order = new Order(-1, new TShirt(Size.L, Color.UNKNOWN), Destination.EUROPE);
        assertEquals("{\"id\":-1, \"tShirt\":{\"size\":\"L\", \"color\":\"UNKNOWN\"}, \"destination\":\"EUROPE\"}",
            order.toString());
    }
}
