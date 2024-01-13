package bg.sofia.uni.fmi.mjt.order.client.order;

import bg.sofia.uni.fmi.mjt.order.client.destination.Destination;
import bg.sofia.uni.fmi.mjt.order.client.tshirt.TShirt;

public record Order(int id, TShirt tShirt, Destination destination) {
}
