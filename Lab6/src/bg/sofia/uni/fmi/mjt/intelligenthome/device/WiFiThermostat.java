package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import java.time.LocalDateTime;

public class WiFiThermostat extends IoTDeviceBase {
    public WiFiThermostat(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime, getUniqueId(name), DeviceType.THERMOSTAT);
    }

    private static String getUniqueId(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(DeviceType.THERMOSTAT.getShortName());
        sb.append('-');
        sb.append(name);
        sb.append('-');
        sb.append(uniqueNumberDevice++);
        return sb.toString();
    }

}