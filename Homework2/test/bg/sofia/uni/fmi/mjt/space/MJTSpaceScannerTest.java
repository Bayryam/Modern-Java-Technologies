package bg.sofia.uni.fmi.mjt.space;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.exception.TimeFrameMismatchException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.Rocket;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MJTSpaceScannerTest {
    private MJTSpaceScanner spaceScanner;
    private MJTSpaceScanner spaceScannerEmpty;
    private SecretKey secretKey;
    private static final String NO_MISSIONS = "";
    private static final String NO_ROCKETS = "";
    private static final String MISSIONS = """
        Unnamed: 0,Company Name,Location,Datum,Detail,Status Rocket," Rocket",Status Mission
        0,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Fri Aug 07, 2020",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,"50.0 ",Success
        1,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Thu Aug 06, 2020",Long March 2D | Gaofen-9 04 & Q-SAT,StatusActive,"29.75 ",Success
        2,SpaceX,"Pad A, Boca Chica, Texas, USA","Tue Aug 04, 2020",Starship Prototype | 150 Meter Hop,StatusActive,,Success
        3,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 30, 2020",Proton-M/Briz-M | Ekspress-80 & Ekspress-103,StatusActive,"65.0 ",Success
        4,ULA,"SLC-41, Cape Canaveral AFS, Florida, USA","Thu Jul 30, 2020",Atlas V 541 | Perseverance,StatusActive,"145.0 ",Success
        5,CASC,"Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China","Sat Jul 25, 2020","Long March 4B | Ziyuan-3 03, Apocalypse-10 & NJU-HKU 1",StatusActive,"64.68 ",Failure
        6,Roscosmos,"Site 200/39, Baikonur Cosmodrome, Kazakhstan","Thu Jul 23, 2020",Soyuz 2.1a | Progress MS-15,StatusActive,"48.5 ",Success
        7,CASC,"LC-101, Wenchang Satellite Launch Center, China","Thu Jul 23, 2020",Long March 5 | Tianwen-1,StatusActive,,Success
        8,SpaceX,"LC-39A, Kennedy Space Center, Florida, USA","Mon Jul 20, 2020",Falcon 9 Block 5 | ANASIS-II,StatusActive,"50.0 ",Success
        9,JAXA,"LA-Y1, Tanegashima Space Center, Japan","Sun Jul 19, 2020",H-IIA 202 | Hope Mars Mission,StatusActive,"90.0 ",Success
        10,Northrop,"LP-0B, Wallops Flight Facility, Virginia, USA","Wed Jul 15, 2020",Minotaur IV | NROL-129,StatusActive,"46.0 ",Failure
        """;
    private static final String ROCKETS = """
        "",Name,Wiki,Rocket Height
        0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m
        1,Tsyklon-4M,https://en.wikipedia.org/wiki/Cyclone-4M,38.7 m
        2,Unha-2,https://en.wikipedia.org/wiki/Unha,28.0 m
        3,Unha-3,https://en.wikipedia.org/wiki/Unha,32.0 m
        4,Vanguard,https://en.wikipedia.org/wiki/Vanguard_(rocket),23.0 m
        5,Vector-H,https://en.wikipedia.org/wiki/Vector-H,18.3 m
        6,Vector-R,https://en.wikipedia.org/wiki/Vector-R,
        7,Vega,,29.9 m
        8,Vega C,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
        9,Vega E,https://en.wikipedia.org/wiki/Vega_(rocket),35.0 m
        10,VLS-1,,19.0 m
        103,Atlas V 541,https://en.wikipedia.org/wiki/Atlas_V,62.2 m
        182,H-IIA 202,https://en.wikipedia.org/wiki/H-IIA,53.0 m
        294,Proton-M/Briz-M,https://en.wikipedia.org/wiki/Proton-M,58.2 m
        """;

    @BeforeEach
    public void setup() {
        Reader missionsReader = new StringReader(MISSIONS);
        Reader rocketsReader = new StringReader(ROCKETS);
        Reader emptyMissionsReader = new StringReader(NO_MISSIONS);
        Reader emptyRocketsReader = new StringReader(NO_ROCKETS);
        secretKey = new SecretKeySpec("SecretValidKey!!".getBytes(StandardCharsets.UTF_8), "AES");
        SecretKey invalidSecretKey = new SecretKeySpec("InvalidKey!".getBytes(StandardCharsets.UTF_8), "AES");

        spaceScanner = new MJTSpaceScanner(missionsReader, rocketsReader, secretKey);
        spaceScannerEmpty = new MJTSpaceScanner(emptyMissionsReader, emptyRocketsReader, invalidSecretKey);
    }

    @Test
    void testGetAllMissions() {
        Collection<Mission> result = spaceScanner.getAllMissions();
        assertEquals(11, result.size());
    }

    @Test
    void testGetAllMissionsEmptyCollectionNoMissions() {
        Collection<Mission> result = spaceScannerEmpty.getAllMissions();
        assertEquals(0, result.size());
    }

    @Test
    void testGetAllMissionsWithStatusThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getAllMissions(null));
    }

    @Test
    void testGetAllMissionsWithStatus() {
        Collection<Mission> result = spaceScanner.getAllMissions(MissionStatus.SUCCESS);
        assertEquals(9, result.size());
    }

    @Test
    void testGetAllMissionsWithStatusNonExistingStatus() {
        Collection<Mission> result = spaceScanner.getAllMissions(MissionStatus.PARTIAL_FAILURE);
        assertEquals(0, result.size());
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getCompanyWithMostSuccessfulMissions(null, null));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsThrowsTimeFrameMismatchException() {
        assertThrows(TimeFrameMismatchException.class,
            () -> spaceScanner.getCompanyWithMostSuccessfulMissions(
                LocalDate.of(2010, 10, 20),
                LocalDate.of(2000, 10, 20)
            ));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissions() {
        assertEquals("SpaceX", spaceScanner.getCompanyWithMostSuccessfulMissions(
            LocalDate.of(2010, 10, 20),
            LocalDate.of(2021, 10, 20)
        ));
    }

    @Test
    void testGetCompanyWithMostSuccessfulMissionsNoMissions() {
        assertEquals("", spaceScannerEmpty.getCompanyWithMostSuccessfulMissions(
            LocalDate.of(2010, 10, 20),
            LocalDate.of(2021, 10, 20)
        ));
    }

    @Test
    void testGetMissionsPerCountryNoMissions() {
        Map<String, Collection<Mission>> result = spaceScannerEmpty.getMissionsPerCountry();
        assertEquals(0, result.size());
    }

    @Test
    void testGetMissionsPerCountry() {
        Map<String, Collection<Mission>> result = spaceScanner.getMissionsPerCountry();
        assertEquals(4, result.size());

        assertEquals(5, result.get("USA").size());
        assertEquals(3, result.get("China").size());
        assertEquals(1, result.get("Japan").size());
        assertEquals(2, result.get("Kazakhstan").size());
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNoMissions() {
        List<Mission> result =
            spaceScannerEmpty.getTopNLeastExpensiveMissions(
                5, MissionStatus.PARTIAL_FAILURE, RocketStatus.STATUS_ACTIVE);
        assertEquals(0, result.size());
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNegativeN() {
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getTopNLeastExpensiveMissions(
                -2, MissionStatus.PARTIAL_FAILURE, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetTopNLeastExpensiveMissionsZeroN() {
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getTopNLeastExpensiveMissions(
                0, MissionStatus.PARTIAL_FAILURE, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetTopNLeastExpensiveMissionsNullArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getTopNLeastExpensiveMissions(
                5, null, RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetTopNLeastExpensiveMissions() {
        List<Mission> missions = spaceScanner.getTopNLeastExpensiveMissions(
            3, MissionStatus.SUCCESS, RocketStatus.STATUS_ACTIVE);

        assertEquals(3, missions.size());
        assertEquals("1", missions.getFirst().id());
        assertEquals("6", missions.get(1).id());
        assertEquals("0", missions.get(2).id());
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompanyNoMissions() {
        Map<String, String> missions = spaceScannerEmpty.getMostDesiredLocationForMissionsPerCompany();
        assertEquals(0, missions.size());
    }

    @Test
    void testGetMostDesiredLocationForMissionsPerCompany() {
        Map<String, String> missions = spaceScanner.getMostDesiredLocationForMissionsPerCompany();
        assertEquals(6, missions.size());
        assertEquals("LC-39A, Kennedy Space Center, Florida, USA", missions.get("SpaceX"));
        assertEquals("Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China", missions.get("CASC"));
        assertEquals("Site 200/39, Baikonur Cosmodrome, Kazakhstan", missions.get("Roscosmos"));
        assertEquals("SLC-41, Cape Canaveral AFS, Florida, USA", missions.get("ULA"));
        assertEquals("LA-Y1, Tanegashima Space Center, Japan", missions.get("JAXA"));
        assertEquals("LP-0B, Wallops Flight Facility, Virginia, USA", missions.get("Northrop"));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyNoMissions() {
        Map<String, String> result =
            spaceScannerEmpty.getLocationWithMostSuccessfulMissionsPerCompany(
                LocalDate.of(2001, 2, 5), LocalDate.of(2005, 3, 4));
        assertEquals(0, result.size());
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(
                null, LocalDate.of(2005, 3, 4)));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompanyThrowsTimeFrameMismatchException() {
        assertThrows(TimeFrameMismatchException.class,
            () -> spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(
                LocalDate.of(2006, 2, 5), LocalDate.of(2005, 3, 4)));
    }

    @Test
    void testGetLocationWithMostSuccessfulMissionsPerCompany() {
        Map<String, String> result = spaceScanner.getLocationWithMostSuccessfulMissionsPerCompany(
            LocalDate.of(2000, 2, 5), LocalDate.of(2021, 3, 4));
        assertEquals("LC-39A, Kennedy Space Center, Florida, USA", result.get("SpaceX"));
        assertEquals("Site 9401 (SLS-2), Jiuquan Satellite Launch Center, China", result.get("CASC"));
        assertEquals("Site 200/39, Baikonur Cosmodrome, Kazakhstan", result.get("Roscosmos"));
        assertEquals("SLC-41, Cape Canaveral AFS, Florida, USA", result.get("ULA"));
        assertEquals("LA-Y1, Tanegashima Space Center, Japan", result.get("JAXA"));
    }

    @Test
    void testGetAllRocketsNoRockets() {
        Collection<Rocket> rockets = spaceScannerEmpty.getAllRockets();
        assertEquals(0, rockets.size());
    }

    @Test
    void testGetAllRockets() {
        Collection<Rocket> rockets = spaceScanner.getAllRockets();
        assertEquals(14, rockets.size());
    }

    @Test
    void testGetTopNTallestRocketsNoRockets() {
        List<Rocket> rockets = spaceScannerEmpty.getTopNTallestRockets(6);
        assertEquals(0, rockets.size());
    }

    @Test
    void testGetTopNTallestRocketsNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNTallestRockets(-1));
    }

    @Test
    void testGetTopNTallestRocketsZeroN() {
        assertThrows(IllegalArgumentException.class, () -> spaceScanner.getTopNTallestRockets(0));
    }

    @Test
    void testGetTopNTallestRockets() {
        List<Rocket> rockets = spaceScanner.getTopNTallestRockets(3);
        assertEquals(3, rockets.size());
        assertEquals("103", rockets.getFirst().id());
        assertEquals("294", rockets.get(1).id());
        assertEquals("182", rockets.get(2).id());
    }

    @Test
    void testGetWikiPageForRocketNoRockets() {
        Map<String, Optional<String>> result = spaceScannerEmpty.getWikiPageForRocket();
        assertEquals(0, result.size());
    }

    @Test
    void testGetWikiPageForRocket() {
        Map<String, Optional<String>> result = spaceScanner.getWikiPageForRocket();
        assertEquals(14, result.size());
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3"), result.get("Tsyklon-3"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Cyclone-4M"), result.get("Tsyklon-4M"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Unha"), result.get("Unha-2"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Unha"), result.get("Unha-3"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Vanguard_(rocket)"), result.get("Vanguard"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Vector-H"), result.get("Vector-H"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Vector-R"), result.get("Vector-R"));
        assertEquals(Optional.empty(), result.get("Vega"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Vega_(rocket)"), result.get("Vega C"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Vega_(rocket)"), result.get("Vega E"));
        assertEquals(Optional.empty(), result.get("VLS-1"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Atlas_V"), result.get("Atlas V 541"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/H-IIA"), result.get("H-IIA 202"));
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Proton-M"), result.get("Proton-M/Briz-M"));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNoMissions() {
        List<String> result =
            spaceScannerEmpty.getWikiPagesForRocketsUsedInMostExpensiveMissions(50, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE);
        assertEquals(0, result.size());
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsZeroN() {
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(0, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNegativeN() {
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(-5, MissionStatus.SUCCESS,
                RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissionsNullArgument() {
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(10, null,
                RocketStatus.STATUS_ACTIVE));
    }

    @Test
    void testGetWikiPagesForRocketsUsedInMostExpensiveMissions() {
        List<String> result = spaceScanner.getWikiPagesForRocketsUsedInMostExpensiveMissions(3, MissionStatus.SUCCESS,
            RocketStatus.STATUS_ACTIVE);
        assertEquals(3, result.size());
        assertEquals("https://en.wikipedia.org/wiki/Atlas_V", result.get(0));
        assertEquals("https://en.wikipedia.org/wiki/H-IIA", result.get(1));
        assertEquals("https://en.wikipedia.org/wiki/Proton-M", result.get(2));
    }

    @Test
    void testSaveMostReliableRocketThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> spaceScanner.saveMostReliableRocket(
                null, LocalDate.of(2020, 12, 15),
                LocalDate.of(2021, 3, 14)));
    }

    @Test
    void testSaveMostReliableRocketThrowsTimeFrameMismatchException() {
        try (OutputStream writer = new ByteArrayOutputStream()) {
            assertThrows(TimeFrameMismatchException.class,
                () -> spaceScanner.saveMostReliableRocket(
                    writer, LocalDate.of(2021, 12, 15),
                    LocalDate.of(2020, 3, 14)));
        } catch (IOException exception) {
            throw new UncheckedIOException("There was a problem with the test!", exception);
        }
    }

    @Test
    void testSaveMostReliableRocketThrowsCipherException() {
        try (OutputStream writer = new ByteArrayOutputStream()) {
            assertThrows(CipherException.class,
                () -> spaceScannerEmpty.saveMostReliableRocket(
                    writer, LocalDate.of(2020, 12, 15),
                    LocalDate.of(2021, 3, 14)));
        } catch (IOException exception) {
            throw new UncheckedIOException("There was a problem with the test!", exception);
        }
    }

    @Test
    public void testSaveMostReliableRocket() throws CipherException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            spaceScanner.saveMostReliableRocket(
                outputStream, LocalDate.of(2019, 2, 15),
                LocalDate.of(2022, 2, 15));

            byte[] bytes = outputStream.toByteArray();
            Rijndael algorithm = new Rijndael(secretKey);
            try (ByteArrayOutputStream resultOutputStream = new ByteArrayOutputStream()) {
                algorithm.decrypt(new ByteArrayInputStream(bytes), resultOutputStream);
                String result = resultOutputStream.toString();
                assertEquals("Atlas V 541", result);
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("There was a problem with the test!", exception);
        }
    }

}
