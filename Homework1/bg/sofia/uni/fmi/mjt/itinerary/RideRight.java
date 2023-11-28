package bg.sofia.uni.fmi.mjt.itinerary;

import bg.sofia.uni.fmi.mjt.itinerary.algorithms.AStarAlgorithm;
import bg.sofia.uni.fmi.mjt.itinerary.algorithms.AStarEdge;
import bg.sofia.uni.fmi.mjt.itinerary.algorithms.CityNode;
import bg.sofia.uni.fmi.mjt.itinerary.algorithms.JourneyEdge;
import bg.sofia.uni.fmi.mjt.itinerary.exception.CityNotKnownException;
import bg.sofia.uni.fmi.mjt.itinerary.exception.NoPathToDestinationException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.SequencedCollection;

public class RideRight implements ItineraryPlanner {

    List<Journey> schedule;

    public RideRight(List<Journey> schedule) {
        this.schedule = schedule;
    }

    private Journey getDirectPath(City start, City destination) {

        Journey result = null;
        BigDecimal cheapestPathCost = AStarAlgorithm.INFINITY;

        for (Journey journey : schedule) {
            BigDecimal cost = journey.getAllCost();
            if (journey.from().equals(start) && journey.to().equals(destination) &&
                    cheapestPathCost.compareTo(cost) > 0) {
                result = journey;
                cheapestPathCost = cost;
            }
        }
        return result;

    }

    private boolean isCityMissingInTheSchedule(City city) {

        for (Journey journey : schedule) {
            if (journey.from().equals(city) || journey.to().equals(city)) {
                return false;
            }
        }
        return true;

    }

    @Override
    public SequencedCollection<Journey> findCheapestPath(City start, City destination, boolean allowTransfer)
            throws CityNotKnownException, NoPathToDestinationException {

        if (isCityMissingInTheSchedule(start) || isCityMissingInTheSchedule(destination)) {
            throw new CityNotKnownException(
                    "Start or destination city is not in the schedule!");
        }

        SequencedCollection<Journey> cheapestPath = new ArrayList<>();
        if (!allowTransfer) {
            Journey toAdd = getDirectPath(start, destination);
            if (toAdd != null) {
                cheapestPath.add(toAdd);
            }
        } else {
            findCheapestPathWithTransfers(start, destination, cheapestPath);
        }

        if (cheapestPath.isEmpty()) {
            throw new NoPathToDestinationException("Path between startCity and destination doesn't exist!");
        }

        return cheapestPath;

    }

    private void findCheapestPathWithTransfers(City start, City destination,
                                               SequencedCollection<Journey> cheapestPath) {

        AStarAlgorithm<City, Journey> alg = new AStarAlgorithm<>();
        CityNode startNode = new CityNode(start, BigDecimal.ZERO);
        CityNode destinationNode = new CityNode(destination, BigDecimal.ZERO);

        SequencedCollection<AStarEdge<Journey>> col = alg.aStar(startNode, destinationNode, getEdges());
        for (AStarEdge<Journey> edge : col) {
            cheapestPath.add(edge.getEdge());
        }

    }

    private List<AStarEdge<Journey>> getEdges() {

        List<AStarEdge<Journey>> result = new ArrayList<>();
        for (Journey j : schedule) {
            JourneyEdge toAdd = new JourneyEdge(j);
            result.add(toAdd);
        }
        return result;

    }
}
