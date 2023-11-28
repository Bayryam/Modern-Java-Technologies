package bg.sofia.uni.fmi.mjt.itinerary.algorithms;

import bg.sofia.uni.fmi.mjt.itinerary.Journey;

import java.math.BigDecimal;

public class JourneyEdge implements AStarEdge<Journey> {

    private final Journey journey;

    public JourneyEdge(Journey journey) {
        this.journey = journey;
    }

    @Override
    public AStarNode getStartNode() {
        return new CityNode(journey.from(), BigDecimal.ZERO);
    }

    @Override
    public AStarNode getFinalNode() {
        return new CityNode(journey.to(), BigDecimal.ZERO);
    }

    @Override
    public BigDecimal getWeight() {
        return journey.getAllCost();
    }

    @Override
    public Journey getEdge() {
        return journey;
    }
}
