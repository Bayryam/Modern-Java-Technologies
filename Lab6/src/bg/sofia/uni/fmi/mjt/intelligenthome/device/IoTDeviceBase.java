package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class IoTDeviceBase implements IoTDevice {
    static int uniqueNumberDevice = 0;
    private final DeviceType type;
    private final String deviceId;
    private final String name;
    private final double powerConsumption;
    private final LocalDateTime installationDateTime;
    private LocalDateTime registration;

    public IoTDeviceBase(String name, double powerConsumption, LocalDateTime installationDateTime,
                         String deviceId, DeviceType type) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
        this.deviceId = deviceId;
        this.type = type;
    }

    @Override
    public String getId() {
        return deviceId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPowerConsumption() {
        return powerConsumption;
    }

    @Override
    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    @Override
    public DeviceType getType() {
        return type;
    }

    @Override
    public long getRegistration() {
        return Duration.between(registration, LocalDateTime.now()).toHours();
    }

    @Override
    public void setRegistration(LocalDateTime registration) {
        this.registration = registration;
    }

    @Override
    public long getPowerConsumptionKWh() {
        long duration = Duration.between(getInstallationDateTime(), LocalDateTime.now()).toHours();
        return (long) (duration * powerConsumption);
    }

}