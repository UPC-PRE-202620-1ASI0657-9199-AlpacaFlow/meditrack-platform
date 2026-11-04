package com.alpacaflow.meditrackplatform.monitoring.domain.model.aggregates;

import com.alpacaflow.meditrackplatform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.entities.*;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * MonitoredDevice aggregate root.
 * <p>Holds the vital sign measurements of one smart patch. The patch itself belongs to the
 * Devices bounded context: this aggregate only keeps its identifier (no foreign key), so the
 * Monitoring Service never reads the Devices database.</p>
 */
@Getter
@Entity
public class MonitoredDevice extends AuditableAbstractAggregateRoot<MonitoredDevice> {

    /** Maximum number of measurements kept per type (one week of daily readings). */
    public static final int MAX_MEASUREMENTS_PER_TYPE = 7;

    @Column(nullable = false, unique = true)
    private Long deviceId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "monitored_device_id")
    @OrderBy("id ASC")
    private List<Measurement> measurements;

    protected MonitoredDevice() {
        this.measurements = new ArrayList<>();
    }

    public MonitoredDevice(Long deviceId) {
        this();
        if (deviceId == null || deviceId <= 0) {
            throw new IllegalArgumentException("Device ID must be a positive number");
        }
        this.deviceId = deviceId;
    }

    public HeartRateMeasurement addHeartRate(int bpm) {
        return add(new HeartRateMeasurement(bpm));
    }

    public TemperatureMeasurement addTemperature(double celsius) {
        return add(new TemperatureMeasurement(celsius));
    }

    public OxygenMeasurement addOxygen(int spo2) {
        return add(new OxygenMeasurement(spo2));
    }

    public BloodPressureMeasurement addBloodPressure(int diastolic, int systolic) {
        return add(new BloodPressureMeasurement(diastolic, systolic));
    }

    /** Adds a measurement, discarding the oldest one of the same type when the limit is reached. */
    private <T extends Measurement> T add(T measurement) {
        var sameType = measurements.stream()
                .filter(m -> m.getClass().equals(measurement.getClass()))
                .toList();
        if (sameType.size() >= MAX_MEASUREMENTS_PER_TYPE) {
            measurements.remove(sameType.getFirst());
        }
        measurements.add(measurement);
        return measurement;
    }

    public <T extends Measurement> List<T> getMeasurementsOfType(Class<T> type) {
        return measurements.stream().filter(type::isInstance).map(type::cast).toList();
    }

    public List<HeartRateMeasurement> getHeartRateMeasurements() {
        return getMeasurementsOfType(HeartRateMeasurement.class);
    }

    public List<TemperatureMeasurement> getTemperatureMeasurements() {
        return getMeasurementsOfType(TemperatureMeasurement.class);
    }

    public List<OxygenMeasurement> getOxygenMeasurements() {
        return getMeasurementsOfType(OxygenMeasurement.class);
    }

    public List<BloodPressureMeasurement> getBloodPressureMeasurements() {
        return getMeasurementsOfType(BloodPressureMeasurement.class);
    }
}
