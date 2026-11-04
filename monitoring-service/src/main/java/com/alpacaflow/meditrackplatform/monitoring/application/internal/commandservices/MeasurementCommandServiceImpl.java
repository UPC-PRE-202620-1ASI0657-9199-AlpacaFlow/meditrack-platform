package com.alpacaflow.meditrackplatform.monitoring.application.internal.commandservices;

import com.alpacaflow.meditrackplatform.monitoring.application.internal.outboundservices.acl.ExternalDeviceService;
import com.alpacaflow.meditrackplatform.monitoring.domain.exceptions.DeviceNotFoundException;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.aggregates.MonitoredDevice;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.commands.*;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.entities.*;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.events.AlertCreatedEvent;
import com.alpacaflow.meditrackplatform.monitoring.domain.services.MeasurementCommandService;
import com.alpacaflow.meditrackplatform.monitoring.infrastructure.persistence.jpa.repositories.MonitoredDeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registers vital sign measurements and raises an AlertCreatedEvent when a value is
 * outside the safe range. The event is published when the aggregate is saved and is
 * handled by AlertCreatedEventHandler, which creates the Alert.
 */
@Service
public class MeasurementCommandServiceImpl implements MeasurementCommandService {

    private final MonitoredDeviceRepository monitoredDeviceRepository;
    private final ExternalDeviceService externalDeviceService;

    public MeasurementCommandServiceImpl(MonitoredDeviceRepository monitoredDeviceRepository,
                                         ExternalDeviceService externalDeviceService) {
        this.monitoredDeviceRepository = monitoredDeviceRepository;
        this.externalDeviceService = externalDeviceService;
    }

    @Override
    @Transactional
    public HeartRateMeasurement handle(AddHeartRateMeasurementToDeviceCommand command) {
        var device = findOrRegister(command.deviceId());
        var measurement = device.addHeartRate(command.bpm());
        if (measurement.surpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getDeviceId(), command.bpm(), measurement.getType().toString()));
        }
        return persist(device, measurement.getClass());
    }

    @Override
    @Transactional
    public TemperatureMeasurement handle(AddTemperatureMeasurementToDeviceCommand command) {
        var device = findOrRegister(command.deviceId());
        var measurement = device.addTemperature(command.celsius());
        if (measurement.surpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getDeviceId(), command.celsius(), measurement.getType().toString()));
        }
        return persist(device, measurement.getClass());
    }

    @Override
    @Transactional
    public OxygenMeasurement handle(AddOxygenMeasurementToDeviceCommand command) {
        var device = findOrRegister(command.deviceId());
        var measurement = device.addOxygen(command.spo2());
        if (measurement.surpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getDeviceId(), command.spo2(), measurement.getType().toString()));
        }
        return persist(device, measurement.getClass());
    }

    @Override
    @Transactional
    public BloodPressureMeasurement handle(AddBloodPressureMeasurementToDeviceCommand command) {
        var device = findOrRegister(command.deviceId());
        var measurement = device.addBloodPressure(command.diastolic(), command.systolic());
        if (measurement.diastolicSurpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getDeviceId(), command.diastolic(), measurement.getType().toString()));
        }
        if (measurement.systolicSurpassesThreshold()) {
            device.addDomainEvent(new AlertCreatedEvent(device.getDeviceId(), command.systolic(), measurement.getType().toString()));
        }
        return persist(device, measurement.getClass());
    }

    /**
     * Saves the aggregate (publishing its domain events) and returns the measurement just
     * added, as stored, so the response carries its generated id.
     */
    @SuppressWarnings("unchecked")
    private <T extends Measurement> T persist(MonitoredDevice device, Class<? extends Measurement> type) {
        var saved = monitoredDeviceRepository.saveAndFlush(device);
        return (T) saved.getMeasurementsOfType(type).getLast();
    }

    /**
     * Returns the local MonitoredDevice. On the first measurement of a device, the
     * Devices Service is asked (through the ACL) whether the device exists.
     */
    private MonitoredDevice findOrRegister(Long deviceId) {
        return monitoredDeviceRepository.findByDeviceId(deviceId).orElseGet(() -> {
            if (!externalDeviceService.existsDeviceById(deviceId)) {
                throw new DeviceNotFoundException(deviceId);
            }
            return new MonitoredDevice(deviceId);
        });
    }
}
