import bg.sofia.uni.fmi.mjt.space.MJTSpaceScanner;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import bg.sofia.uni.fmi.mjt.space.mission.Mission;
import bg.sofia.uni.fmi.mjt.space.mission.MissionStatus;
import bg.sofia.uni.fmi.mjt.space.rocket.RocketStatus;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class Main {
    public static void main(String... args) throws IOException, CipherException {
        Reader misReader = new FileReader(new File("all-missions-from-1957 (1).csv"));
        Reader rockReader = new FileReader(new File("all-rockets-from-1957 (1).csv"));
        SecretKey key = new SecretKeySpec(new byte[] {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}, "AES");
        MJTSpaceScanner scanner = new MJTSpaceScanner(misReader, rockReader, key);
        Map<String, Collection<Mission>> res = scanner.getMissionsPerCountry();
        System.out.println("s");
    }
}
