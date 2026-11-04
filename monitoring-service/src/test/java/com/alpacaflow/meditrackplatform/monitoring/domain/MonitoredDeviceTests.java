package com.alpacaflow.meditrackplatform.monitoring.domain;

import com.alpacaflow.meditrackplatform.monitoring.domain.model.aggregates.MonitoredDevice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Domain rules of the MonitoredDevice aggregate (no database or Spring context needed). */
class MonitoredDeviceTests {

    @Test
    void heartRateInsideSafeRangeDoesNotSurpassThreshold() {
        var device = new MonitoredDevice(1L);
        var measurement = device.addHeartRate(78);
        assertFalse(measurement.surpassesThreshold());
    }

    @Test
    void heartRateOutsideSafeRangeSurpassesThreshold() {
        var device = new MonitoredDevice(1L);
        assertTrue(device.addHeartRate(145).surpassesThreshold());
        assertTrue(device.addHeartRate(40).surpassesThreshold());
    }

    @Test
    void keepsOnlyTheLatestSevenMeasurementsPerType() {
        var device = new MonitoredDevice(1L);
        for (int bpm = 61; bpm <= 70; bpm++) device.addHeartRate(bpm);
        device.addOxygen(97);

        var heartRates = device.getHeartRateMeasurements();
        assertEquals(MonitoredDevice.MAX_MEASUREMENTS_PER_TYPE, heartRates.size());
        assertEquals(64, heartRates.getFirst().getBpm());
        assertEquals(70, heartRates.getLast().getBpm());
        assertEquals(1, device.getOxygenMeasurements().size());
    }

    @Test
    void rejectsInvalidDeviceId() {
        assertThrows(IllegalArgumentException.class, () -> new MonitoredDevice(0L));
        assertThrows(IllegalArgumentException.class, () -> new MonitoredDevice(null));
    }
}
