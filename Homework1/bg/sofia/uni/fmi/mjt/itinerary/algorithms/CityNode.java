package bg.sofia.uni.fmi.mjt.itinerary.algorithms;

import bg.sofia.uni.fmi.mjt.itinerary.City;

import java.math.BigDecimal;
import java.util.Objects;

public class CityNode implements Comparable<CityNode>, AStarNode {
    private final City city;
    private BigDecimal fScore;

    public CityNode(City city, BigDecimal fScore) {
        this.city = city;
        this.fScore = fScore;
    }

    public BigDecimal getFScore() {
        return fScore;
    }

    @Override
    public void setFScore(BigDecimal fScore) {
        this.fScore = fScore;
    }

    @Override
    public String getValueName() {
        return city.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CityNode cityNode = (CityNode) o;
        return Objects.equals(city, cityNode.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city);
    }

    @Override
    public int compareTo(CityNode other) {
        if (fScore.compareTo(other.fScore) == 0) {
            return city.name().compareTo(other.city.name());
        }
        return fScore.compareTo(other.fScore);
    }
}
