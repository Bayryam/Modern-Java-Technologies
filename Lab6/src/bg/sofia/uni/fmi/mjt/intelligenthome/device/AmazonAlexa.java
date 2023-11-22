package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import java.time.LocalDateTime;

public class AmazonAlexa extends IoTDeviceBase {

    public AmazonAlexa(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime, getUniqueId(name), DeviceType.SMART_SPEAKER);
    }

    private static String getUniqueId(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(DeviceType.SMART_SPEAKER.getShortName());
        sb.append('-');
        sb.append(name);
        sb.append('-');
        sb.append(uniqueNumberDevice++);
        return sb.toString();
    }

}