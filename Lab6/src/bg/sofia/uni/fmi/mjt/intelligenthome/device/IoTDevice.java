package bg.sofia.uni.fmi.mjt.intelligenthome.device;

import java.time.LocalDateTime;

public interface IoTDevice {
    /**
     * Returns the ID of the device.
     */
    String getId();

    /**
     * Returns the name of the device.
     */
    String getName();

    /**
     * Returns the power consumption of the device. For example, a bulb may consume
     * 30W/hour.
     */
    double getPowerConsumption();

    /**
     * Returns the date and time of device installation. This is a time in the past
     * when the device was 'physically' installed. It is not related to the time
     * when the device is registered in the Home.
     */
    LocalDateTime getInstallationDateTime();

    /**
     * Returns the type of the device.
     */
    DeviceType getType();

    long getRegistration();

    void setRegistration(LocalDateTime registration);

    /**
     * Returns the total power consumption of a device which is calculated by the hours elapsed
     * between the two LocalDateTime-s: the installation time and the current time (now)
     * multiplied by the stated nominal hourly power consumption of the device.
     */
    long getPowerConsumptionKWh();
}