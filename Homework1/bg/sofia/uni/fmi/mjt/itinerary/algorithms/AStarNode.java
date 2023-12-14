package bg.sofia.uni.fmi.mjt.itinerary.algorithms;

import java.math.BigDecimal;

public interface AStarNode {
    BigDecimal getFScore();

    void setFScore(BigDecimal fScore);

    String getValueName();
}
