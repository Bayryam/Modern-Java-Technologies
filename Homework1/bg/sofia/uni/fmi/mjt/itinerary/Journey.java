package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.vehicle.VehicleType;

import java.math.BigDecimal;

public record Journey(VehicleType vehicleType, City from, City to, BigDecimal price) {

    private static final BigDecimal METRES_IN_KILOMETRE = BigDecimal.valueOf(1000);
    private static final BigDecimal DOLLARS_PER_KILOMETRE = BigDecimal.valueOf(20);

    private BigDecimal getPriceWithTaxCost() {

        BigDecimal taxCost = price.multiply(vehicleType.getGreenTax());
        return price.add(taxCost);

    }

    private BigDecimal getManhattanDistanceCost() {

        BigDecimal manhattanDistanceInKM = BigDecimal.valueOf(Math.abs(from.location().x() - to.location().x()) +
                Math.abs(from.location().y() - to.location().y())).divide(METRES_IN_KILOMETRE);
        return manhattanDistanceInKM.multiply(DOLLARS_PER_KILOMETRE);

    }

    public BigDecimal getAllCost() {
        return getPriceWithTaxCost().add(getManhattanDistanceCost());
    }
}
