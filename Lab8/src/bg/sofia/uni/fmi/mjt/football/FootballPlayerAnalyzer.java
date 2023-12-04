package bg.sofia.uni.fmi.mjt.football;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FootballPlayerAnalyzer {
    private static final int MAX_SIMILARITY_DIFF = 3;
    private static final int INVALID_LINE_COUNT = 1;

    private final List<Player> players;

    /**
     * Loads the dataset from the given {@code reader}. The reader argument will not be null and a correct dataset of
     * the specified type can be read from it.
     *
     * @param reader Reader from which the dataset can be read.
     */
    public FootballPlayerAnalyzer(Reader reader) {

        try (var fileReader = new BufferedReader(reader)) {
            players = fileReader.lines().skip(INVALID_LINE_COUNT).map(Player::of).toList();
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read information from file!", exception);
        }

    }

    /**
     * Returns all players from the dataset in undefined order as an unmodifiable List. If the dataset is empty, returns
     * an empty List.
     *
     * @return the list of all players.
     */
    public List<Player> getAllPlayers() {
        return players;
    }

    /**
     * Returns an unmodifiable set of all nationalities in the dataset. If the dataset is empty, returns an empty Set.
     *
     * @return the set of all nationalities
     */
    public Set<String> getAllNationalities() {
        return players
                .stream()
                .map(Player::nationality)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns the highest paid player from the provided nationality. If there are two or more players with equal
     * maximum wage, returns any of them.
     *
     * @param nationality the nationality of the player to return
     * @return the highest paid player
     * @throws IllegalArgumentException in case the provided nationality is null
     * @throws NoSuchElementException   in case there is no player with the provided nationality
     */
    public Player getHighestPaidPlayerByNationality(String nationality) {

        if (nationality == null) {
            throw new IllegalArgumentException("Nationality argument is null!");
        }

        return players
                .stream()
                .filter(p -> p.nationality().equals(nationality))
                .max(Comparator.comparingDouble(Player::wageEuro))
                .orElseThrow();

    }

    /**
     * Returns a breakdown of players by position. Note that some players can play in more than one position, so they
     * should be present in more than one value Set. If no player plays in a given Position then that position should
     * not be present as a key in the map.
     *
     * @return a Map with key: a Position and value: the set of players in the dataset that can play in that Position,
     * in undefined order.
     */
    public Map<Position, Set<Player>> groupByPosition() {

        return players.stream()
                .flatMap(player -> player.positions()
                        .stream()
                        .map(pos -> Map.entry(pos, player)))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())));

    }

    /**
     * Returns an Optional containing the top prospect player in the dataset that can play in the provided position and
     * that can be bought with the provided budget considering the player's value_euro. If no player can be bought with
     * the provided budget then return an empty Optional.
     * <p>
     * The player's prospect is calculated by the following formula: Prospect = (r + p) ÷ a where r is the player's
     * overall rating, p is the player's potential and a is the player's age
     *
     * @param position the position in which the player should be able to play
     * @param budget   the available budget for buying a player
     * @return an Optional containing the top prospect player
     * @throws IllegalArgumentException in case the provided position is null or the provided budget is negative
     */
    public Optional<Player> getTopProspectPlayerForPositionInBudget(Position position, long budget) {
        if (position == null) {
            throw new IllegalArgumentException("Position argument is null!");
        }

        if (budget < 0) {
            throw new IllegalArgumentException("Invalid budget argument!");
        }

        return players
                .stream()
                .filter(p -> p.positions().contains(position))
                .filter(p -> p.valueEuro() <= budget)
                .max(Comparator.comparingDouble(p -> (p.overallRating() + p.potential()) % p.age()));
    }

    /**
     * Returns an unmodifiable set of players that are similar to the provided player. Two players are considered
     * similar if: 1. there is at least one position in which both of them can play 2. both players prefer the same foot
     * 3. their overall_rating measures differ by at most 3 (inclusive)
     * If the dataset contains the provided player, the player will be present in the returned result.
     *
     * @param player the player for whom similar players are retrieved. It may or may not be part of the dataset.
     * @return an unmodifiable set of similar players
     * @throws IllegalArgumentException if the provided player is null
     */
    public Set<Player> getSimilarPlayers(Player player) {

        if (player == null) {
            throw new IllegalArgumentException("Player argument is null!");
        }

        return players.stream().filter(p -> {
            Set<Position> intersection = new HashSet<>(p.positions());
            intersection.retainAll(new HashSet<>(player.positions()));
            return !intersection.isEmpty() &&
                    p.preferredFoot().equals(player.preferredFoot()) &&
                    Math.abs(p.overallRating() - player.overallRating()) <= MAX_SIMILARITY_DIFF;
        }).collect(Collectors.toUnmodifiableSet());

    }

    /**
     * Returns an unmodifiable set of players whose full name contains the provided keyword (case-sensitive search)
     *
     * @param keyword the keyword that should be contained in player's full name
     * @return an unmodifiable set of players
     * @throws IllegalArgumentException if the provided keyword is null
     */
    public Set<Player> getPlayersByFullNameKeyword(String keyword) {

        if (keyword == null) {
            throw new IllegalArgumentException("Keyword argument is null!");
        }

        return players
                .stream()
                .filter(p -> p.fullName().contains(keyword))
                .collect(Collectors.toUnmodifiableSet());

    }

}
