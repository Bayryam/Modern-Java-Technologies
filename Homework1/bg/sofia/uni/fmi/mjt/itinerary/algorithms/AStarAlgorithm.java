package bg.sofia.uni.fmi.mjt.itinerary.algorithms;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.SequencedCollection;
import java.util.Set;

public class AStarAlgorithm<T, P> {

    public static final BigDecimal INFINITY = BigDecimal.valueOf(Double.MAX_VALUE);

    public SequencedCollection<AStarEdge<P>> aStar(AStarNode startNode, AStarNode endNote,
                                                   List<AStarEdge<P>> edges) {

        Queue<AStarNode> openPriorityQueue = new PriorityQueue<>();
        Set<AStarNode> closedSet = new HashSet<>();
        Map<AStarNode, AStarNode> cameFrom = new HashMap<>();
        Map<AStarNode, BigDecimal> gScore = new HashMap<>();
        Map<AStarNode, BigDecimal> fScore = new HashMap<>();

        prepareTheAlgorithm(startNode, endNote, edges, gScore, fScore, openPriorityQueue);

        while (!openPriorityQueue.isEmpty()) {
            AStarNode current = openPriorityQueue.poll();

            if (current.equals(endNote)) {
                return reconstructPath(cameFrom, endNote, edges);
            }

            closedSet.add(current);

            evaluateNeighbors(endNote, edges, current, gScore,
                    closedSet, openPriorityQueue, cameFrom, fScore);
        }

        return null;

    }

    private AStarEdge<P> findEdgeBetweenTheNodes(AStarNode from, AStarNode to,
                                                 List<AStarEdge<P>> edges) {

        AStarEdge<P> result = null;
        BigDecimal cheapestPath = INFINITY;
        for (AStarEdge<P> edge : edges) {
            BigDecimal weight = edge.getWeight();
            if (edge.getStartNode().equals(from) &&
                    edge.getFinalNode().equals(to) && cheapestPath.compareTo(weight) > 0) {
                result = edge;
                cheapestPath = weight;
            }
        }
        return result;

    }

    private List<AStarEdge<P>> getEdgesWithThisNodeAsStart(AStarNode startNode,
                                                           List<AStarEdge<P>> edges) {

        List<AStarEdge<P>> neighbors = new ArrayList<>();
        for (AStarEdge<P> edge : edges) {
            if (edge.getStartNode().equals(startNode)) {
                neighbors.add(edge);
            }
        }
        return neighbors;

    }

    private void evaluateNeighbors(AStarNode endNode,
                                   List<AStarEdge<P>> edges, AStarNode currentNode,
                                   Map<AStarNode, BigDecimal> gScore, Set<AStarNode> closedSet,
                                   Queue<AStarNode> openPriorityQueue, Map<AStarNode,
                                   AStarNode> cameFrom,
                                   Map<AStarNode, BigDecimal> fScore) {

        for (AStarEdge<P> edge : getEdgesWithThisNodeAsStart(currentNode, edges)) {
            AStarNode neighbor = edge.getFinalNode();
            BigDecimal tentativeGScore = gScore.getOrDefault(currentNode, BigDecimal.ZERO)
                    .add(edge.getWeight());

            if (closedSet.contains(neighbor)) {
                continue;
            }

            if (!openPriorityQueue.contains(neighbor) ||
                    tentativeGScore.compareTo(gScore.getOrDefault(neighbor, INFINITY)) < 0) {
                updateOpenPriorityQueue(endNode, edges, cameFrom,
                        neighbor, currentNode, gScore, tentativeGScore, fScore, openPriorityQueue);
            }
        }

    }

    private void updateOpenPriorityQueue(AStarNode endNode, List<AStarEdge<P>> edges,
                                         Map<AStarNode, AStarNode> cameFrom, AStarNode neighbor,
                                         AStarNode currentNode, Map<AStarNode, BigDecimal> gScore,
                                         BigDecimal tentativeGScore, Map<AStarNode, BigDecimal> fScore,
                                         Queue<AStarNode> openPriorityQueue) {

        cameFrom.put(neighbor, currentNode);
        gScore.put(neighbor, tentativeGScore);
        BigDecimal weight = tentativeGScore.add(calculateHeuristic(neighbor, endNode, edges));
        fScore.put(neighbor, weight);
        neighbor.setFScore(weight);
        openPriorityQueue.add(neighbor);

    }

    private void prepareTheAlgorithm(AStarNode start, AStarNode destination, List<AStarEdge<P>> schedule,
                                     Map<AStarNode, BigDecimal> gScore, Map<AStarNode, BigDecimal> fScore,
                                     Queue<AStarNode> openPriorityQueue) {

        gScore.put(start, BigDecimal.ZERO);
        fScore.put(start, calculateHeuristic(start, destination, schedule));
        openPriorityQueue.add(start);

    }

    public BigDecimal calculateHeuristic(AStarNode currentNode, AStarNode endNode,
                                         List<AStarEdge<P>> edges) {

        Map<AStarNode, BigDecimal> remainingWeights = new HashMap<>();
        Set<AStarNode> visited = new HashSet<>();
        Queue<AStarNode> queue = new PriorityQueue<>();

        queue.offer(currentNode);

        while (!queue.isEmpty()) {
            AStarNode current = queue.poll();

            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);

            if (current.equals(endNode)) {
                return current.getFScore();
            }

            updateRemainingWeights(edges, remainingWeights, queue, current);
        }

        return INFINITY;

    }

    private void updateRemainingWeights(List<AStarEdge<P>> edges, Map<AStarNode, BigDecimal> remainingWeights,
                                        Queue<AStarNode> queue, AStarNode current) {

        for (AStarEdge<P> edge : edges) {

            if (edge.getStartNode().equals(current)) {

                BigDecimal tentativeWeight = current.getFScore().add(edge.getWeight());
                if (isEligible(remainingWeights, current, edge, tentativeWeight)) {
                    remainingWeights.put(edge.getFinalNode(), tentativeWeight);
                    AStarNode toAdd = edge.getFinalNode();
                    toAdd.setFScore(tentativeWeight);
                    queue.offer(toAdd);
                }

            }

        }

    }

    private boolean isEligible(Map<AStarNode, BigDecimal> remainingWeights, AStarNode current, AStarEdge<P> edge, BigDecimal tentativeWeight) {

        return !remainingWeights.containsKey(edge.getFinalNode()) ||
                remainingWeights.get(edge.getFinalNode()).compareTo(tentativeWeight) > 0 ||
                (remainingWeights.get(edge.getFinalNode()).compareTo(tentativeWeight) == 0 &&
                        edge.getFinalNode().getValueName().compareTo(current.getValueName()) < 0);

    }

    private SequencedCollection<AStarEdge<P>> reconstructPath(Map<AStarNode,
            AStarNode> cameFrom, AStarNode current, List<AStarEdge<P>> edges) {

        List<AStarEdge<P>> totalPath = new ArrayList<>();

        while (cameFrom.containsKey(current)) {
            totalPath.add(findEdgeBetweenTheNodes(cameFrom.get(current), current, edges));
            current = cameFrom.get(current);
        }

        Collections.reverse(totalPath);
        return totalPath;

    }

}