package bg.sofia.uni.fmi.order.server;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ResponseTest {
    @Test
    void testCreate() {
        Response response = Response.create(5);
        assertNull(response.orders());
        assertEquals("ORDER_ID=5", response.additionalInfo());
        assertEquals("{\"status\":\"CREATED\", \"additionalInfo\":\"ORDER_ID=5\"}", response.toString());
    }

    @Test
    void testOk() {
        Response response = Response.ok(List.of(
            new Order(3, new TShirt(Size.S, Color.BLACK), Destination.EUROPE),
            new Order(4, new TShirt(Size.S, Color.BLACK), Destination.EUROPE)));
        assertEquals("", response.additionalInfo());
        assertEquals(2, response.orders().size());
        assertEquals(
            "{\"status\":\"OK\", \"orders\":[{\"id\":3, \"tShirt\":{\"size\":\"S\", \"color\":\"BLACK\"}," +
                " \"destination\":\"EUROPE\"}, {\"id\":4, \"tShirt\":{\"size\":\"S\", \"color\":\"BLACK\"}, \"destination\":\"EUROPE\"}]}",
            response.toString());
    }

    @Test
    void testDecline() {
        Response response = Response.decline("Message!");
        assertEquals("Message!", response.additionalInfo());
        assertNull(response.orders());
        assertEquals("{\"status\":\"DECLINED\", \"additionalInfo\":\"Message!\"}",
            response.toString());
    }

    @Test
    void testNotFound() {
        Response response = Response.notFound(1);
        assertEquals("Order with id = 1 does not exist.", response.additionalInfo());
        assertNull(response.orders());
        assertEquals("{\"status\":\"NOT_FOUND\", \"additionalInfo\":\"Order with id = 1 does not exist.\"}",
            response.toString());
    }
}
