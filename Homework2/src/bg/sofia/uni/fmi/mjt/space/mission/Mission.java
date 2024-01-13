package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Mission(String id, String company, String location, LocalDate date, Detail detail,
                      RocketStatus rocketStatus, Optional<Double> cost, MissionStatus missionStatus) {
    private static final String DATE_FORMATTER = "E MMM dd, yyyy";
    private static final String DELIMITER = ", ";
    private static final char COST_DELIMITER = ',';
    private static final int ID_GROUP = 1;
    private static final int COMPANY_NAME_GROUP = 2;
    private static final int LOCATION_GROUP = 3;
    private static final int DATE_GROUP = 4;
    private static final int ROCKET_NAME_GROUP = 5;
    private static final int PAYLOAD_GROUP = 6;
    private static final int ROCKET_STATUS_GROUP = 7;
    private static final int COST_GROUP = 9;
    private static final int MISSION_STATUS_GROUP = 10;
    private static final String PATTERN = "^(.*?),(.*?),\"(.*?)\",\"(.*?)\",(.*?) \\| (.*?),(.*?),(\"(.*?)\")?,(.*)$";
    private static final String EMPTY_STRING = "";

    private static LocalDate getDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        return LocalDate.parse(date, formatter);
    }

    private static RocketStatus getRocketStatus(String status) {
        return status.equals(RocketStatus.STATUS_RETIRED.toString())
            ? RocketStatus.STATUS_RETIRED : RocketStatus.STATUS_ACTIVE;
    }

    private static MissionStatus getMissionStatus(String status) {
        for (MissionStatus missionStatus : MissionStatus.values()) {
            if (status.equals(missionStatus.toString())) {
                return missionStatus;
            }
        }

        throw new IllegalArgumentException("Invalid status!");
    }

    private static Optional<Double> getCost(String cost) {
        if (cost == null) {
            return Optional.empty();
        }

        cost = cost.replace(String.valueOf(COST_DELIMITER), EMPTY_STRING);
        return Optional.of(Double.parseDouble(cost));
    }

    public static Mission of(String line) {
        Pattern r = Pattern.compile(PATTERN);
        Matcher m = r.matcher(line);
        if (m.find()) {
            return new Mission(m.group(ID_GROUP), m.group(COMPANY_NAME_GROUP), m.group(LOCATION_GROUP),
                getDate(m.group(DATE_GROUP)), new Detail(m.group(ROCKET_NAME_GROUP), m.group(PAYLOAD_GROUP)),
                getRocketStatus(m.group(ROCKET_STATUS_GROUP)),
                getCost(m.group(COST_GROUP)),
                getMissionStatus(m.group(MISSION_STATUS_GROUP)));
        } else {
            return null;
        }
    }

    public boolean isMissionInGivenPeriod(LocalDate from, LocalDate to) {
        return (date.isAfter(from) && date.isBefore(to)) || date.isEqual(from) || date.isEqual(to);
    }

    public boolean isMissionSuccessful() {
        return missionStatus.equals(MissionStatus.SUCCESS);
    }

    public String getCountry() {
        String[] locationProperties = location.split(DELIMITER);
        return locationProperties[locationProperties.length - 1];
    }
}
