package bg.sofia.uni.fmi.mjt.football;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public record Player(String name, String fullName, LocalDate birthDate, int age,
                     double heightCm, double weightKg, List<Position> positions, String nationality,
                     int overallRating, int potential, long valueEuro, long wageEuro, Foot preferredFoot) {

    private static final String PLAYER_ATTRIBUTE_DELIMITER = ";";
    private static final String POSITION_ATTRIBUTE_DELIMITER = ",";
    private static final String PLAYER_LEFT_FOOT_ATTRIBUTE = "Left";
    private static final String DATE_FORMATTER = "M/d/yyyy";
    private static final int NAME_TOKEN_CONSTANT = 0;
    private static final int FULL_NAME_TOKEN_CONSTANT = 1;
    private static final int BIRTH_DATE_TOKEN_CONSTANT = 2;
    private static final int AGE_TOKEN_CONSTANT = 3;
    private static final int HEIGHT_CM_TOKEN_CONSTANT = 4;
    private static final int WEIGHT_KG_TOKEN_CONSTANT = 5;
    private static final int POSITIONS_TOKEN_CONSTANT = 6;
    private static final int NATIONALITY_TOKEN_CONSTANT = 7;
    private static final int OVERALL_RATING_TOKEN_CONSTANT = 8;
    private static final int POTENTIAL_TOKEN_CONSTANT = 9;
    private static final int VALUE_EURO_TOKEN_CONSTANT = 10;
    private static final int WAGE_EURO_TOKEN_CONSTANT = 11;
    private static final int PREFERRED_FOOT_TOKEN_CONSTANT = 12;

    private static List<Position> getPositions(String positions) {
        return Arrays.stream(positions.split(POSITION_ATTRIBUTE_DELIMITER))
                .map(Position::valueOf)
                .toList();
    }

    private static LocalDate getDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMATTER);
        return LocalDate.parse(date, formatter);
    }

    public static Player of(String line) {

        final String[] tokens = line.split(PLAYER_ATTRIBUTE_DELIMITER);
        String nameToken = tokens[NAME_TOKEN_CONSTANT];
        String fullNameToken = tokens[FULL_NAME_TOKEN_CONSTANT];
        LocalDate birthDay = getDate(tokens[BIRTH_DATE_TOKEN_CONSTANT]);
        int ageToken = Integer.parseInt(tokens[AGE_TOKEN_CONSTANT]);
        double heightCmToken = Double.parseDouble(tokens[HEIGHT_CM_TOKEN_CONSTANT]);
        double weightKgToken = Double.parseDouble(tokens[WEIGHT_KG_TOKEN_CONSTANT]);
        List<Position> positions = getPositions(tokens[POSITIONS_TOKEN_CONSTANT]);
        String nationality = tokens[NATIONALITY_TOKEN_CONSTANT];
        int overallRatingToken = Integer.parseInt(tokens[OVERALL_RATING_TOKEN_CONSTANT]);
        int potentialToken = Integer.parseInt(tokens[POTENTIAL_TOKEN_CONSTANT]);
        long valueEuroToken = Long.parseLong(tokens[VALUE_EURO_TOKEN_CONSTANT]);
        long wageEuroToken = Long.parseLong(tokens[WAGE_EURO_TOKEN_CONSTANT]);
        Foot preferredFootToken =
                tokens[PREFERRED_FOOT_TOKEN_CONSTANT].equals(PLAYER_LEFT_FOOT_ATTRIBUTE) ? Foot.LEFT : Foot.RIGHT;

        return new Player(nameToken, fullNameToken, birthDay,
                ageToken, heightCmToken, weightKgToken,
                positions, nationality, overallRatingToken,
                potentialToken, valueEuroToken, wageEuroToken, preferredFootToken);
    }
}
