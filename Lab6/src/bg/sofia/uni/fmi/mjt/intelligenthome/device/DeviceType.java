package bg.sofia.uni.fmi.mjt.intelligenthome.device;

public enum DeviceType {
    SMART_SPEAKER("SPKR"), BULB("BLB"), THERMOSTAT("TMST");

    private final String shortName;

    DeviceType(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}