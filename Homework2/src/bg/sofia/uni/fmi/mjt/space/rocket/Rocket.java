package bg.sofia.uni.fmi.mjt.space.rocket;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Rocket(String id, String name, Optional<String> wiki, Optional<Double> height) {
    private static final String PATTERN = "^(.*?),(\"(.*?)\"|(.*?)),(.*?),([0-9]*(\\.)*[0-9]*)";
    private static final int ID_GROUP = 1;
    private static final int NAME_GROUP = 2;
    private static final int WIKI_GROUP = 5;
    private static final int HEIGHT_GROUP = 6;

    private static Optional<Double> getHeight(String height) {
        if (!height.isEmpty()) {
            return Optional.of(Double.parseDouble(height));
        }
        return Optional.empty();
    }

    private static Optional<String> getWiki(String wiki) {
        if (!wiki.isEmpty()) {
            return Optional.of(wiki);
        }
        return Optional.empty();
    }

    public static Rocket of(String line) {
        Pattern r = Pattern.compile(PATTERN);
        Matcher m = r.matcher(line);

        if (m.find()) {
            return new Rocket(m.group(ID_GROUP), m.group(NAME_GROUP), getWiki(m.group(WIKI_GROUP)),
                getHeight(m.group(HEIGHT_GROUP)));
        } else {
            return null;
        }
    }
}
