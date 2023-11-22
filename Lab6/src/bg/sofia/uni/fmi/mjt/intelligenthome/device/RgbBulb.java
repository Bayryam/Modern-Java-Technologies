package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import java.time.LocalDateTime;

public class RgbBulb extends IoTDeviceBase {
    public RgbBulb(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime, getUniqueId(name), DeviceType.BULB);
    }

    private static String getUniqueId(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(DeviceType.BULB.getShortName());
        sb.append('-');
        sb.append(name);
        sb.append('-');
        sb.append(uniqueNumberDevice++);
        return sb.toString();
    }

}