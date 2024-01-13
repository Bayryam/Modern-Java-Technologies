package bg.sofia.uni.fmi.mjt.order.server.repository;

import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.server.order.Order;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.TShirt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class MJTOrderRepository implements OrderRepository {
    private static final int FIELDS_COUNT = 3;
    private static final String DELIMITER = ",";
    private static final String INVALID_MESSAGE = "invalid: ";
    private static final String INVALID_SIZE = "size";
    private static final String INVALID_COLOR = "color";
    private static final String INVALID_DESTINATION = "destination";
    private static final int INVALID_ORDER_ID = -1;
    private int idGenerator = 1;
    private final Queue<Order> orders;

    public MJTOrderRepository() {
        orders = new LinkedBlockingQueue<>();
    }

    private Size determineSize(String stringRepresentationOfSize) {
        for (Size size : Size.values()) {
            if (size.getName().equals(stringRepresentationOfSize)) {
                return Size.valueOf(stringRepresentationOfSize);
            }
        }
        return Size.UNKNOWN;
    }

    private Color determineColor(String stringRepresentationOfColor) {
        for (Color color : Color.values()) {
            if (color.getName().equals(stringRepresentationOfColor)) {
                return Color.valueOf(stringRepresentationOfColor);
            }
        }
        return Color.UNKNOWN;
    }

    private Destination determineDestination(String stringRepresentationOfDestination) {
        for (Destination destination : Destination.values()) {
            if (destination.getName().equals(stringRepresentationOfDestination)) {
                return Destination.valueOf(stringRepresentationOfDestination);
            }
        }
        return Destination.UNKNOWN;
    }

    private List<String> getInvalidFields(Size size, Color color, Destination destination) {
        List<String> result = new ArrayList<>(FIELDS_COUNT);

        if (size.equals(Size.UNKNOWN)) {
            result.add(INVALID_SIZE);
        }

        if (color.equals(Color.UNKNOWN)) {
            result.add(INVALID_COLOR);
        }

        if (destination.equals(Destination.UNKNOWN)) {
            result.add(INVALID_DESTINATION);
        }

        return result;
    }

    @Override
    public Response request(String size, String color, String destination) {
        if (size == null || color == null || destination == null) {
            throw new IllegalArgumentException("There is a null argument!");
        }

        Size tShirtSize = determineSize(size);
        Color tShirtColor = determineColor(color);
        Destination tShirtDestination = determineDestination(destination);

        Collection<String> invalidFields = getInvalidFields(tShirtSize, tShirtColor, tShirtDestination);

        if (invalidFields.isEmpty()) {
            orders.add(new Order(idGenerator, new TShirt(tShirtSize, tShirtColor), tShirtDestination));
            return Response.create(idGenerator++);
        }
        orders.add(new Order(INVALID_ORDER_ID, new TShirt(tShirtSize, tShirtColor), tShirtDestination));

        String errorMessage = INVALID_MESSAGE + String.join(DELIMITER, invalidFields);
        return Response.decline(errorMessage);
    }

    @Override
    public Response getOrderById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id is non-positive number!");
        }
        Order foundOrder;

        boolean exists = orders.stream().anyMatch(order -> order.id() == id);
        if (!exists) {
            return Response.notFound(id);
        }
        foundOrder = orders.stream().filter(order -> order.id() == id).toList().getFirst();

        return Response.ok(List.of(foundOrder));
    }

    @Override
    public Response getAllOrders() {
        return Response.ok(orders);
    }

    @Override
    public Response getAllSuccessfulOrders() {
        Collection<Order> result;
        result = orders.stream().filter(order -> order.id() != INVALID_ORDER_ID).toList();
        return Response.ok(result);
    }
}
