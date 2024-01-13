package bg.sofia.uni.fmi.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MJTOrderRepositoryTest {
    private MJTOrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        orderRepository = new MJTOrderRepository();
    }

    @Test
    void testRequestNullArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> orderRepository.request(null, null, null));
    }

    @Test
    void testRequestValidData() {
        Response response = orderRepository.request("M", "RED", "EUROPE");
        assertEquals("ORDER_ID=1", response.additionalInfo());
    }

    @Test
    void testRequestInvalidSize() {
        Response response = orderRepository.request("K", "RED", "EUROPE");
        assertEquals("invalid: size", response.additionalInfo());
    }

    @Test
    void testRequestInvalidColor() {
        Response response = orderRepository.request("M", "ORANGE", "EUROPE");
        assertEquals("invalid: color", response.additionalInfo());
    }

    @Test
    void testRequestInvalidDestination() {
        Response response = orderRepository.request("M", "RED", "EUROE");
        assertEquals("invalid: destination", response.additionalInfo());
    }

    @Test
    void testRequestInvalidSizeColorAndDestination() {
        Response response = orderRepository.request("K", "RE", "EUROE");
        assertEquals("invalid: size,color,destination", response.additionalInfo());
    }

    @Test
    void testGetOrderByIdNonPositiveId() {
        assertThrows(IllegalArgumentException.class, () -> orderRepository.getOrderById(-2));
    }

    @Test
    void testGetOrderByIdNotFound() {
        Response response = orderRepository.getOrderById(8);
        assertEquals("Order with id = 8 does not exist.", response.additionalInfo());
    }

    @Test
    void testGetOrderByIdFound() {
        orderRepository.request("M", "RED", "EUROPE");
        Response response = orderRepository.getOrderById(1);
        assertEquals(1, response.orders().size());
        assertEquals(1, response.orders().iterator().next().id());
    }

    @Test
    void testGetAllOrders() {
        orderRepository.request("M", "RED", "EUROPE");
        orderRepository.request("M", "BLACK", "EUROPE");
        Response response = orderRepository.getAllOrders();
        assertEquals(2, response.orders().size());
    }

    @Test
    void testGetAllOrdersEmptyOrders() {
        Response response = orderRepository.getAllOrders();
        assertEquals(0, response.orders().size());
    }

    @Test
    void testGetAllSuccessfulOrdersNoSuccessful() {
        orderRepository.request("M", "R", "EUROPE");
        Response response = orderRepository.getAllSuccessfulOrders();
        assertEquals(0, response.orders().size());
    }

    @Test
    void testGetAllSuccessfulOrders() {
        orderRepository.request("M", "R", "EUROPE");
        orderRepository.request("M", "RED", "EUROPE");
        Response response = orderRepository.getAllSuccessfulOrders();
        assertEquals(1, response.orders().size());
    }
}
