package bg.sofia.uni.fmi.mjt.football;

import org.junit.jupiter.api.Test;

import javax.swing.text.DateFormatter;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FootballPlayerAnalyzerTest {

    static final String INPUT_DATA_1 = """
            name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
            L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
            C. Eriksen;Christian  Dannemann Eriksen;2/14/1992;27;154.94;76.2;CAM,RM,CM;Denmark;88;89;69500000;205000;Right
            P. Pogba;Paul Pogba;3/15/1993;25;190.5;83.9;CM,CAM;France;88;91;73000000;255000;Right
            L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;91;88;62000000;165000;Left
            K. Koulibaly;Kalidou Koulibaly;6/20/1991;27;187.96;88.9;CB;Senegal;88;91;60000000;135000;Right
            V. van Dijk;Virgil van Dijk;7/8/1991;27;193.04;92.1;CB;Netherlands;88;90;59500000;215000;Right
            K. Mbappé;Kylian Mbappé;12/20/1998;20;162.56;73;RW,ST,RM;France;95;95;81000000;100000;Left
            S. Agüero;Sergio Leonel Agüero del Castillo;6/2/1988;30;172.72;69.9;ST;Argentina;89;89;64500000;300000;Right
            M. Neuer;Manuel Neuer;3/27/1986;32;193.04;92.1;GK;Argentina;89;89;38000000;130000;Right
            E. Cavani;Edinson Roberto Cavani Gómez;2/14/1987;32;185.42;77.1;ST;Uruguay;97;89;60000000;200000;Right
            Sergio Busquets;Sergio Busquets i Burgos;7/16/1988;30;187.96;76.2;CDM,CM;Spain;89;89;51500000;315000;Right
            T. Courtois;Thibaut Courtois;5/11/1992;26;198.12;96.2;GK;Belgium;89;90;53500000;240000;Left
            """;

    static final String INPUT_DATA_2 = """
            name;full_name;birth_date;age;height_cm;weight_kgs;positions;nationality;overall_rating;potential;value_euro;wage_euro;preferred_foot
            L. Messi;Lionel Andrés Messi Cuccittini;6/24/1987;31;170.18;72.1;CF,RW,ST;Argentina;94;94;110500000;565000;Left
            L. Insigne;Lorenzo Insigne;6/4/1991;27;162.56;59;LW,ST;Italy;91;88;62000000;165000;Left
            K. Mbappé;Kylian Mbappé;12/20/1998;20;162.56;73;RW,ST,RM;France;95;95;81000000;100000;Left
            T. Courtois;Thibaut Courtois;5/11/1992;26;198.12;96.2;GK;Belgium;89;90;53500000;240000;Left
            """;

    static final String INPUT_DATA_EMPTY = "";

    static final Player TEST_PLAYER1 = new Player("T. Courtois", "Thibaut Courtois",
            LocalDate.parse("5/11/1992", DateTimeFormatter.ofPattern("M/d/yyyy")), 26, 198.12,
            96.2, List.of(Position.GK), "Belgium",
            89, 90, 53500000, 240000, Foot.LEFT);

    static final Player TEST_PLAYER2 = new Player("L. Messi", "Lionel Andrés Messi Cuccittini",
            LocalDate.parse("6/24/1987", DateTimeFormatter.ofPattern("M/d/yyyy")), 31, 170.18,
            72.1, List.of(Position.CF, Position.RW, Position.ST), "Argentina",
            94, 94, 110500000, 565000, Foot.LEFT);

    static final Player SIMILAR_PLAYER_1 = new Player("K. Mbappé", "Kylian Mbappé",
            LocalDate.parse("12/20/1998", DateTimeFormatter.ofPattern("M/d/yyyy")), 20, 162.56,
            73, List.of(Position.RW, Position.ST, Position.RM), "France",
            95, 95, 81000000, 100000, Foot.LEFT);

    static final Player SIMILAR_PLAYER_2 = new Player("L. Insigne", "Lorenzo Insigne",
            LocalDate.parse("6/4/1991", DateTimeFormatter.ofPattern("M/d/yyyy")), 27, 162.56,
            59, List.of(Position.LW, Position.ST), "Italy",
            91, 88, 62000000, 165000, Foot.LEFT);

    Reader reader1 = new StringReader(INPUT_DATA_1);
    Reader reader2 = new StringReader(INPUT_DATA_2);
    Reader readerEmpty = new StringReader(INPUT_DATA_EMPTY);
    FootballPlayerAnalyzer fba1 = new FootballPlayerAnalyzer(reader1);
    FootballPlayerAnalyzer fba2 = new FootballPlayerAnalyzer(reader2);
    FootballPlayerAnalyzer fbaEmpty = new FootballPlayerAnalyzer(readerEmpty);

    @Test
    void testGetAllPlayers() {
        assertEquals(List.of(TEST_PLAYER2, SIMILAR_PLAYER_2, SIMILAR_PLAYER_1, TEST_PLAYER1), fba2.getAllPlayers());
    }

    @Test
    void testGetAllPlayersNoPlayers() {
        assertEquals(List.of(), fbaEmpty.getAllPlayers());
    }

    @Test
    void testGetAllNationalities() {
        assertEquals(Set.of("Argentina", "Italy", "France", "Belgium"), fba2.getAllNationalities());
    }

    @Test
    void testGetAllNationalitiesNoNationalities() {
        assertEquals(Set.of(), fbaEmpty.getAllNationalities());
    }

    @Test
    void testGetHighestPaidPlayerByNationalityNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> fba1.getHighestPaidPlayerByNationality(null));
    }

    @Test
    void testGetHighestPaidPlayerByNationalityNoSuchElement() {
        assertThrows(NoSuchElementException.class, () -> fba1.getHighestPaidPlayerByNationality("Japan"));
    }

    @Test
    void testGetHighestPaidPlayerByNationalityExistingPlayer() {
        assertEquals(TEST_PLAYER2, fba1.getHighestPaidPlayerByNationality("Argentina"));
    }

    @Test
    void testGroupByPosition() {
        Map<Position, Set<Player>> expected = new HashMap<>();
        expected.put(Position.CF, Set.of(TEST_PLAYER2));
        expected.put(Position.RW, Set.of(TEST_PLAYER2, SIMILAR_PLAYER_1));
        expected.put(Position.ST, Set.of(TEST_PLAYER2, SIMILAR_PLAYER_2, SIMILAR_PLAYER_1));
        expected.put(Position.LW, Set.of(SIMILAR_PLAYER_2));
        expected.put(Position.RM, Set.of(SIMILAR_PLAYER_1));
        expected.put(Position.GK, Set.of(TEST_PLAYER1));
        assertEquals(expected, fba2.groupByPosition());
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetPositionArgumentNull() {
        assertThrows(IllegalArgumentException.class,
                () -> fba1.getTopProspectPlayerForPositionInBudget(null, 500));
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetNegativeBudget() {
        assertThrows(IllegalArgumentException.class,
                () -> fba1.getTopProspectPlayerForPositionInBudget(Position.GK, -10));
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetEnoughBudget() {
        assertEquals(Optional.of(SIMILAR_PLAYER_2), fba2.getTopProspectPlayerForPositionInBudget(Position.ST, 1000000000));
    }

    @Test
    void testGetTopProspectPlayerForPositionInBudgetInsufficientBudget() {
        assertEquals(Optional.empty(), fba2.getTopProspectPlayerForPositionInBudget(Position.ST, 0));
    }

    @Test
    void testGetSimilarPlayersNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> fba1.getSimilarPlayers(null));
    }

    @Test
    void testGetSimilarPlayers() {
        assertEquals(Set.of(TEST_PLAYER2, SIMILAR_PLAYER_1, SIMILAR_PLAYER_2), fba1.getSimilarPlayers(TEST_PLAYER2));
    }

    @Test
    void testGetSimilarPlayersNoSimilarPlayers() {
        assertEquals(Set.of(TEST_PLAYER1), fba1.getSimilarPlayers(TEST_PLAYER1));
    }

    @Test
    void testGetPlayersByFullNameKeywordNullKeyword() {
        assertThrows(IllegalArgumentException.class, () -> fba1.getPlayersByFullNameKeyword(null));
    }

    @Test
    void testGetPlayersByFullNameKeywordNoMatch() {
        assertEquals(Set.of(), fba1.getPlayersByFullNameKeyword("Ronaldo"));
    }

    @Test
    void testGetPlayersByFullNameKeywordMatches() {
        assertEquals(Set.of(TEST_PLAYER2, TEST_PLAYER1), fba2.getPlayersByFullNameKeyword("C"));
    }


}
