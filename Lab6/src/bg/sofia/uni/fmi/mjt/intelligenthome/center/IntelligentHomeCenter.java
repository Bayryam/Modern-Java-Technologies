package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.KWhComparator;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.comparator.RegistrationComparator;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;

import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;

import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class IntelligentHomeCenter {
    private final DeviceStorage storage;

    public IntelligentHomeCenter(DeviceStorage storage) {
        this.storage = storage;
    }

    /**
     * Adds a @device to the IntelligentHomeCenter.
     *
     * @throws IllegalArgumentException         in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already
     *                                          registered.
     */
    public void register(IoTDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException("Device cannot be null!");
        }

        if (storage.exists(device.getId())) {
            throw new DeviceAlreadyRegisteredException("There is a device with the same id in the storage!");
        }

        device.setRegistration(LocalDateTime.now());
        storage.store(device.getId(), device);
    }

    /**
     * Removes the @device from the IntelligentHomeCenter.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException  in case the @device is not found.
     */
    public void unregister(IoTDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("Device cannot be null!");
        }

        if (!storage.exists(device.getId())) {
            throw new DeviceNotFoundException("The device is not present in the storage!");
        }

        storage.delete(device.getId());
    }

    /**
     * Returns a IoTDevice with an ID @id if found.
     *
     * @throws IllegalArgumentException in case @id is null or empty.
     * @throws DeviceNotFoundException  in case device with ID @id is not found.
     */
    public IoTDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("Device cannot be null!");
        }

        if (id.isBlank()) {
            throw new IllegalArgumentException("Device cannot be empty!");
        }

        if (!storage.exists(id)) {
            throw new DeviceNotFoundException("Device not found!");
        }
        return storage.get(id);
    }

    /**
     * Returns the total number of devices with type @type registered in
     * SmartCityHub.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null!");
        }

        int quantity = 0;

        for (IoTDevice device : storage.listAll()) {
            if ((device.getType()).equals(type)) {
                quantity++;
            }
        }

        return quantity;
    }

    /**
     * Returns a collection of IDs of the top @n devices which consumed
     * the most power from the time of their installation until now.
     * <p>
     * The total power consumption of a device is calculated by the hours elapsed
     * between the two LocalDateTime-s: the installation time and the current time (now)
     * multiplied by the stated nominal hourly power consumption of the device.
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<String> getTopNDevicesByPowerConsumption(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count argument cannot be negative!");
        }

        List<IoTDevice> list = new ArrayList<>(storage.listAll());
        list.sort(new KWhComparator());

        if (count > list.size()) {
            count = list.size();
        }

        List<String> arrList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            arrList.add(list.get(i).getId());
        }
        return arrList;
    }

    /**
     * Returns a collection of the first @n registered devices, i.e. the first @n that were added
     * in the IntelligentHomeCenter (registration != installation).
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<IoTDevice> getFirstNDevicesByRegistration(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count argument cannot be negative!");
        }

        List<IoTDevice> list = new LinkedList<>(storage.listAll());

        list.sort(new RegistrationComparator());

        List<IoTDevice> arrList = new ArrayList<>();

        if (count > list.size()) {
            count = list.size();
        }

        for (int i = 0; i < count; i++) {
            arrList.add(list.get(i));
        }

        return arrList;
    }
}