package bg.sofia.uni.fmi.mjt.intelligenthome.center;

import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceAlreadyRegisteredException;
import bg.sofia.uni.fmi.mjt.intelligenthome.center.exceptions.DeviceNotFoundException;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.AmazonAlexa;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.DeviceType;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.IoTDevice;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.RgbBulb;
import bg.sofia.uni.fmi.mjt.intelligenthome.device.WiFiThermostat;
import bg.sofia.uni.fmi.mjt.intelligenthome.storage.DeviceStorage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IntelligentHomeCenterTest {

    @Mock
    DeviceStorage deviceStorage;
    @InjectMocks
    IntelligentHomeCenter intelligentHomeCenter;
    IoTDevice device1 = new RgbBulb("Bulb1", 8.0, LocalDateTime.now().minusHours(5));
    IoTDevice device2 = new RgbBulb("Bulb2", 3.0, LocalDateTime.now().minusHours(6));
    IoTDevice device3 = new RgbBulb("Bulb3", 7.0, LocalDateTime.now().minusHours(7));
    IoTDevice device4 = new AmazonAlexa("Alexa1", 5.0, LocalDateTime.now().minusHours(3));
    IoTDevice device5 = new WiFiThermostat("Thermostat1", 3.0, LocalDateTime.now().minusHours(3));

    @Test
    void testRegisterWithNullDeviceThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.register(null));
    }

    @Test
    void testRegisterWithAlreadyRegisteredThrowsDeviceAlreadyRegisteredException() {
        when(deviceStorage.exists(any())).thenReturn(true);
        assertThrows(DeviceAlreadyRegisteredException.class, () -> intelligentHomeCenter.register(device1));
    }

    @Test
    void testRegisterWithValidDevice() {
        when(deviceStorage.exists(any())).thenReturn(false);
        assertDoesNotThrow(() -> intelligentHomeCenter.register(device1));
    }

    @Test
    void testUnregisterWithNullDeviceThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.unregister(null));
    }

    @Test
    void testUnregisterWithNotRegisteredThrowsDeviceNotFoundException() {
        when(deviceStorage.exists(any())).thenReturn(false);
        assertThrows(DeviceNotFoundException.class, () -> intelligentHomeCenter.unregister(device1));
    }

    @Test
    void testUnregisterRegisteredDevice() {
        when(deviceStorage.exists(any())).thenReturn(true);
        assertDoesNotThrow(() -> intelligentHomeCenter.unregister(device1));
    }

    @Test
    void testGetDeviceByIdNull() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.getDeviceById(null));
    }

    @Test
    void testGetDeviceByIdEmpty() {
        assertThrows(IllegalArgumentException.class, () -> intelligentHomeCenter.getDeviceById(""));
    }

    @Test
    void testGetDeviceByIdNotExistingIdThrowsDeviceNotFoundException() {
        when(deviceStorage.exists("3")).thenReturn(false);
        assertThrows(DeviceNotFoundException.class, () -> intelligentHomeCenter.getDeviceById("3"));
    }

    @Test
    void testGetDeviceByIdValid() {
        when(deviceStorage.exists("existingID")).thenReturn(true);
        assertDoesNotThrow(() -> intelligentHomeCenter.getDeviceById("existingID"));
    }

    @Test
    void testGetDeviceQuantityPerTypeNull() {
        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.getDeviceQuantityPerType(null));
    }

    @Test
    void testGetDeviceQuantityPerTypeEmpty() {
        when(deviceStorage.listAll()).thenReturn(List.of(device1, device2, device3, device4));
        assertEquals(0, intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.THERMOSTAT));
    }

    @Test
    void testGetDeviceQuantityPerTypeEmptyList() {
        when(deviceStorage.listAll()).thenReturn(List.of());
        assertEquals(0, intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.THERMOSTAT));
    }

    @Test
    void testGetDeviceQuantityPerTypeThreeMatches() {
        when(deviceStorage.listAll()).thenReturn(List.of(device1, device2, device3, device4, device5));
        assertEquals(3, intelligentHomeCenter.getDeviceQuantityPerType(DeviceType.BULB));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionNegativeCount() {
        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.getTopNDevicesByPowerConsumption(-1));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionCountMoreThanSize() {
        when(deviceStorage.listAll()).thenReturn(List.of(device1, device2, device3, device4));
        assertEquals(List.of(device3.getId(), device1.getId(), device2.getId(), device4.getId()),
                intelligentHomeCenter.getTopNDevicesByPowerConsumption(5));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionTop2() {
        when(deviceStorage.listAll()).thenReturn(List.of(device1, device2, device3, device4));
        assertEquals(List.of(device3.getId(), device1.getId()),
                intelligentHomeCenter.getTopNDevicesByPowerConsumption(2));
    }

    @Test
    void testGetTopNDevicesByPowerConsumptionEmpty() {
        when(deviceStorage.listAll()).thenReturn(List.of());
        assertEquals(List.of(),
                intelligentHomeCenter.getTopNDevicesByPowerConsumption(2));
    }

    @Test
    void testGetFirstNDevicesByRegistrationNegativeCount() {
        assertThrows(IllegalArgumentException.class,
                () -> intelligentHomeCenter.getFirstNDevicesByRegistration(-1));
    }

    @Test
    void testGetFirstNDevicesByRegistrationMoreCountThanSize() {
        device1.setRegistration(LocalDateTime.now().plusHours(5));
        device2.setRegistration(LocalDateTime.now().plusHours(6));
        device3.setRegistration(LocalDateTime.now().plusHours(7));
        device4.setRegistration(LocalDateTime.now().plusHours(8));
        when(deviceStorage.listAll()).thenReturn(List.of(device3, device2, device1, device4));
        assertEquals(List.of(device4, device3, device2, device1),
                intelligentHomeCenter.getFirstNDevicesByRegistration(5));
    }

    @Test
    void testGetFirstNDevicesByRegistrationMoreCountTop2() {
        device1.setRegistration(LocalDateTime.now().plusHours(5));
        device2.setRegistration(LocalDateTime.now().plusHours(6));
        device3.setRegistration(LocalDateTime.now().plusHours(7));
        device4.setRegistration(LocalDateTime.now().plusHours(8));
        when(deviceStorage.listAll()).thenReturn(List.of(device3, device2, device1, device4));
        assertEquals(List.of(device4, device3),
                intelligentHomeCenter.getFirstNDevicesByRegistration(2));
    }

    @Test
    void testGetFirstNDevicesByRegistrationEmptyStorage() {
        when(deviceStorage.listAll()).thenReturn(List.of());
        assertEquals(List.of(),
                intelligentHomeCenter.getFirstNDevicesByRegistration(2));
    }
}