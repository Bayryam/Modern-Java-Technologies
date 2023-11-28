package bg.sofia.uni.fmi.mjt.itinerary.algorithms;

import java.math.BigDecimal;

public interface AStarEdge<P> {

    AStarNode getStartNode();

    AStarNode getFinalNode();

    BigDecimal getWeight();

    P getEdge();

}
