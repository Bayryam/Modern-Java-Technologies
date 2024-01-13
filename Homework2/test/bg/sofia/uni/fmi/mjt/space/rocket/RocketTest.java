package bg.sofia.uni.fmi.mjt.space.rocket;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RocketTest {
    public static final String LINE = "0,Tsyklon-3,https://en.wikipedia.org/wiki/Tsyklon-3,39.0 m";
    public static final String LINE_WITHOUT_HEIGHT = "14,Vostok-2,https://en.wikipedia.org/wiki/Vostok-2_(rocket),";
    public static final String LINE_WITHOUT_WIKI_AND_HEIGHT = "62,Atlas-E/F Burner,,";
    public static final String INVALID_LINE = "INVALID";
    public static final String LINE_WITH_EXTRA_COMMAS = "150,\"Delta IV Medium+ (5,4)\",https://en.wikipedia.org/wiki/Delta_IV,66.4 m";

    @Test
    void testOfWithoutEmptyOptionals() {
        Rocket rocket = Rocket.of(LINE);
        assertNotNull(rocket);
        assertEquals("0", rocket.id());
        assertEquals("Tsyklon-3", rocket.name());
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Tsyklon-3"), rocket.wiki());
        assertEquals(Optional.of(39.0), rocket.height());
    }

    @Test
    void testOfWithLineWithoutHeight() {
        Rocket rocket = Rocket.of(LINE_WITHOUT_HEIGHT);
        assertNotNull(rocket);
        assertEquals(Optional.empty(), rocket.height());
    }

    @Test
    void testOfWithLineWithoutWikiAndHeight() {
        Rocket rocket = Rocket.of(LINE_WITHOUT_WIKI_AND_HEIGHT);
        assertNotNull(rocket);
        assertEquals(Optional.empty(), rocket.wiki());
        assertEquals(Optional.empty(), rocket.height());
    }

    @Test
    void testOfWithLineWithCommaInTheName() {
        Rocket rocket = Rocket.of(LINE_WITH_EXTRA_COMMAS);
        assertNotNull(rocket);
        assertEquals("150", rocket.id());
        assertEquals("\"Delta IV Medium+ (5,4)\"", rocket.name());
        assertEquals(Optional.of("https://en.wikipedia.org/wiki/Delta_IV"), rocket.wiki());
        assertEquals(Optional.of(66.4), rocket.height());
    }

    @Test
    void testOfWithInvalidLine() {
        Rocket rocket = Rocket.of(INVALID_LINE);
        assertNull(rocket);
    }
}
