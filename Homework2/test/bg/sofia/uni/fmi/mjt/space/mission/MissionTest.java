package bg.sofia.uni.fmi.mjt.space.mission;

import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MissionTest {
    private static final String VALID_LINE_ACTIVE_MISSION_STATUS =
        "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \",Success";
    private static final String VALID_LINE_RETIRED_ROCKET_STATUS =
        "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusRetired,\"50.0 \",Success";
    private static final String INVALID_MISSION_STATUS_LINE =
        "0,SpaceX,\"LC-39A, Kennedy Space Center, Florida, USA\",\"Fri Aug 07, 2020\",Falcon 9 Block 5 | Starlink V1 L9 & BlackSky,StatusActive,\"50.0 \",InvalidMissionStatus";
    private static final String LINE_WITHOUT_COST =
        "2,SpaceX,\"Pad A, Boca Chica, Texas, USA\",\"Tue Aug 04, 2020\",Starship Prototype | 150 Meter Hop,StatusActive,,Success";
    private static final String INVALID_LINE = "InvalidLine";

    private static final Mission SUCCESSFUL_MISSION = new Mission("1", "MyCompany", "Sofia, Bulgaria",
        LocalDate.of(2020, 8, 7), new Detail("myRocket", "123"),
        RocketStatus.STATUS_ACTIVE, Optional.of(12.2), MissionStatus.SUCCESS);
    private static final Mission UNSUCCESSFUL_MISSION = new Mission("1", "MyCompany", "Sofia, Bulgaria",
        LocalDate.of(2020, 8, 7), new Detail("myRocket", "123"),
        RocketStatus.STATUS_ACTIVE, Optional.of(12.2), MissionStatus.FAILURE);

    @Test
    void testOfWithActiveMissionStatus() {
        Mission mission = Mission.of(VALID_LINE_ACTIVE_MISSION_STATUS);
        assertNotNull(mission);
        assertEquals("0", mission.id());
        assertEquals("SpaceX", mission.company());
        assertEquals("LC-39A, Kennedy Space Center, Florida, USA", mission.location());
        assertEquals(LocalDate.of(2020, 8, 7), mission.date());
        assertEquals("Falcon 9 Block 5", mission.detail().rocketName());
        assertEquals("Starlink V1 L9 & BlackSky", mission.detail().payload());
        assertEquals(RocketStatus.STATUS_ACTIVE, mission.rocketStatus());
        assertEquals(Optional.of(50.0), mission.cost());
        assertEquals(MissionStatus.SUCCESS, mission.missionStatus());
    }

    @Test
    void testOfWithRetiredMissionStatus() {
        Mission mission = Mission.of(VALID_LINE_RETIRED_ROCKET_STATUS);
        assertNotNull(mission);
        assertEquals(RocketStatus.STATUS_RETIRED, mission.rocketStatus());
    }

    @Test
    void testOfWithInvalidMissionStatus() {
        assertThrows(IllegalArgumentException.class, () -> Mission.of(INVALID_MISSION_STATUS_LINE));
    }

    @Test
    void testOfWithoutCost() {
        Mission mission = Mission.of(LINE_WITHOUT_COST);
        assertNotNull(mission);
        assertEquals(Optional.empty(), mission.cost());
    }

    @Test
    void testOfWithInvalidLine() {
        Mission mission = Mission.of(INVALID_LINE);
        assertNull(mission);
    }

    @Test
    void testIsMissionInGivenPeriodWithDateOutsideBounds() {
        assertFalse(SUCCESSFUL_MISSION.isMissionInGivenPeriod(
            LocalDate.of(2030, 4, 12), LocalDate.of(2031, 4, 12)));
    }

    @Test
    void testIsMissionInGivenPeriodWithDateInsideBounds() {
        assertTrue(SUCCESSFUL_MISSION.isMissionInGivenPeriod(
            LocalDate.of(2010, 4, 12), LocalDate.of(2031, 4, 12)));
    }

    @Test
    void testIsMissionSuccessfulWithSuccessfulMission() {
        assertTrue(SUCCESSFUL_MISSION.isMissionSuccessful());
    }

    @Test
    void testIsMissionSuccessfulWithUnsuccessfulMission() {
        assertFalse(UNSUCCESSFUL_MISSION.isMissionSuccessful());
    }

    @Test
    void testGetCountry() {
        assertEquals("Bulgaria", UNSUCCESSFUL_MISSION.getCountry());
    }


}
