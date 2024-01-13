package bg.sofia.uni.fmi.mjt.order.server;

import bg.sofia.uni.fmi.mjt.order.server.order.Order;

import java.util.Collection;

public record Response(Status status, String additionalInfo, Collection<Order> orders) {
    private enum Status {
        OK, CREATED, DECLINED, NOT_FOUND
    }

    private static final String CREATED_INFO = "ORDER_ID=%d";
    private static final String NOT_FOUND_INFO = "Order with id = %d does not exist.";
    private static final String EMPTY_STRING = "";

    /**
     * Creates a response
     *
     * @param id order id
     * @return response with status Status.CREATED and with proper message for additional info
     */
    public static Response create(int id) {
        return new Response(Status.CREATED, String.format(CREATED_INFO, id), null);
    }

    /**
     * Creates a response
     *
     * @param orders the orders which will be returned to the client
     * @return response with status Status.OK and Collection of orders
     */
    public static Response ok(Collection<Order> orders) {
        return new Response(Status.OK, EMPTY_STRING, orders);
    }

    /**
     * Creates a response
     *
     * @param errorMessage the message which will be sent as additionalInfo
     * @return response with status Status.DECLINED and errorMessage as additionalInfo
     */
    public static Response decline(String errorMessage) {
        return new Response(Status.DECLINED, errorMessage, null);
    }

    /**
     * Creates a response
     *
     * @param id order id
     * @return response with status Status.NOT_FOUND and with proper message for additional info
     */
    public static Response notFound(int id) {
        return new Response(Status.NOT_FOUND, String.format(NOT_FOUND_INFO, id), null);
    }

    @Override
    public String toString() {
        if (!status.equals(Status.OK)) {
            return String.format("{\"status\":\"%s\", \"additionalInfo\":\"%s\"}", status, additionalInfo);
        }
        return String.format("{\"status\":\"%s\", \"orders\":", status) +
            orders +
            "}";
    }
}