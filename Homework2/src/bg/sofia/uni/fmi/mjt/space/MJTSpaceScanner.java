package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Detail;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.Reader;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MJTSpaceScanner implements SpaceScannerAPI {
    private static final int INVALID_LINE_COUNT = 1;
    private static final double DEFAULT_RELIABILITY = 0.0;
    private static final String EMPTY_STRING = "";
    private final List<Mission> missions;
    private final List<Rocket> rockets;
    private final SecretKey secretKey;

    public MJTSpaceScanner(Reader missionsReader, Reader rocketsReader, SecretKey secretKey) {
        var bufferedMissionsReader = new BufferedReader(missionsReader);
        var bufferedRocketsReader = new BufferedReader(rocketsReader);
        missions = bufferedMissionsReader.lines().skip(INVALID_LINE_COUNT).map(Mission::of).toList();
        rockets = bufferedRocketsReader.lines().skip(INVALID_LINE_COUNT).map(Rocket::of).toList();
        this.secretKey = secretKey;
    }

    @Override
    public Collection<Mission> getAllMissions() {
        return missions;
    }

    @Override
    public Collection<Mission> getAllMissions(MissionStatus missionStatus) {
        if (missionStatus == null) {
            throw new IllegalArgumentException("Mission Status argument is null!");
        }

        return missions.stream()
            .filter(mission -> mission.missionStatus().equals(missionStatus))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getCompanyWithMostSuccessfulMissions(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("There is a null argument!");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException(String.format("To: %s is before From: %s", to, from));
        }

        return missions.stream()
            .filter(mission -> mission.isMissionInGivenPeriod(from, to) && mission.isMissionSuccessful())
            .collect(Collectors.groupingBy(Mission::company, Collectors.counting()))
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(EMPTY_STRING);
    }

    @Override
    public Map<String, Collection<Mission>> getMissionsPerCountry() {
        return missions.stream()
            .collect(Collectors.groupingBy(Mission::getCountry, Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public List<Mission> getTopNLeastExpensiveMissions(int n, MissionStatus missionStatus, RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException(String.format("%d is invalid N!", n));
        }

        if (missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("There is a status that is null!");
        }

        return missions.stream()
            .filter(
                mission -> mission.missionStatus().equals(missionStatus) && mission.rocketStatus().equals(rocketStatus))
            .filter(mission -> mission.cost().isPresent())
            .sorted(Comparator.comparingDouble(mission -> mission.cost().get()))
            .limit(n)
            .toList();
    }

    @Override
    public Map<String, String> getMostDesiredLocationForMissionsPerCompany() {
        return missions.stream()
            .collect(Collectors.groupingBy(
                Mission::company,
                Collectors.groupingBy(Mission::location, Collectors.counting())
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(EMPTY_STRING)
            ));
    }

    @Override
    public Map<String, String> getLocationWithMostSuccessfulMissionsPerCompany(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("There is a null argument!");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException(String.format("To: %s is before From: %s", to, from));
        }

        return missions.stream()
            .filter(mission -> mission.isMissionInGivenPeriod(from, to))
            .filter(Mission::isMissionSuccessful)
            .collect(Collectors.groupingBy(
                Mission::company,
                Collectors.groupingBy(Mission::location, Collectors.counting())
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(EMPTY_STRING)
            ));
    }

    @Override
    public Collection<Rocket> getAllRockets() {
        return rockets;
    }

    @Override
    public List<Rocket> getTopNTallestRockets(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(String.format("%d is invalid N!", n));
        }

        return rockets.stream()
            .filter(rocket -> rocket.height().isPresent())
            .sorted(Comparator.comparing(rocket -> rocket.height().get(),
                Comparator.reverseOrder()))
            .limit(n)
            .toList();
    }

    @Override
    public Map<String, Optional<String>> getWikiPageForRocket() {
        return rockets.stream().collect(Collectors.toMap(Rocket::name, Rocket::wiki));
    }

    @Override
    public List<String> getWikiPagesForRocketsUsedInMostExpensiveMissions(int n, MissionStatus missionStatus,
                                                                          RocketStatus rocketStatus) {
        if (n <= 0) {
            throw new IllegalArgumentException(String.format("%d is invalid N!", n));
        }

        if (missionStatus == null || rocketStatus == null) {
            throw new IllegalArgumentException("There is a status that is null!");
        }

        return missions.stream()
            .filter(mission -> mission.missionStatus().equals(missionStatus) &&
                mission.rocketStatus().equals(rocketStatus) &&
                mission.cost().isPresent())
            .sorted(Comparator.comparing(mission -> mission.cost().get(), Comparator.reverseOrder()))
            .limit(n)
            .map(Mission::detail)
            .map(Detail::rocketName)
            .distinct()
            .flatMap(rocketName -> rockets.stream()
                .filter(rocket -> rocketName.equals(rocket.name()))
                .map(Rocket::wiki)
                .filter(Optional::isPresent)
                .map(Optional::get))
            .toList();

    }

    @Override
    public void saveMostReliableRocket(OutputStream outputStream, LocalDate from, LocalDate to) throws CipherException {
        if (from == null || to == null || outputStream == null) {
            throw new IllegalArgumentException("There is a null argument!");
        }

        if (to.isBefore(from)) {
            throw new TimeFrameMismatchException(String.format("To: %s is before From: %s", to, from));
        }

        String mostReliableRocket = determineMostReliableRocket(from, to);

        try (var inputStream = new ByteArrayInputStream(mostReliableRocket.getBytes())) {
            Rijndael algorithm = new Rijndael(secretKey);
            algorithm.encrypt(inputStream, outputStream);
        } catch (Exception exception) {
            throw new CipherException("There was a problem with the encrypting!", exception);
        }
    }

    private double calculateReliability(Rocket rocket, LocalDate from, LocalDate to) {
        List<Mission> rocketMissions = missions.stream()
            .filter(mission -> mission.detail().rocketName().equals(rocket.name()))
            .filter(mission -> mission.isMissionInGivenPeriod(from, to))
            .toList();

        long successfulMissions = rocketMissions.stream()
            .filter(Mission::isMissionSuccessful)
            .count();

        long failedMissions = rocketMissions.size() - successfulMissions;

        long totalMissions = rocketMissions.size();

        if (totalMissions == 0) {
            return DEFAULT_RELIABILITY;
        }

        return (2.0 * successfulMissions + failedMissions) / (2.0 * totalMissions);
    }

    private String determineMostReliableRocket(LocalDate from, LocalDate to) {

        return rockets.stream()
            .max(Comparator.comparingDouble(rocket -> calculateReliability(rocket, from, to)))
            .map(Rocket::name)
            .orElse(EMPTY_STRING);
    }
}