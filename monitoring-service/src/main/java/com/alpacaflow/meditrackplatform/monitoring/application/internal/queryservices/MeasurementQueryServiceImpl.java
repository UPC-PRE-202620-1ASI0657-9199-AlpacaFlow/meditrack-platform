package com.alpacaflow.meditrackplatform.monitoring.application.internal.queryservices;

import com.alpacaflow.meditrackplatform.monitoring.domain.model.aggregates.MonitoredDevice;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.entities.*;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.queries.*;
import com.alpacaflow.meditrackplatform.monitoring.domain.services.MeasurementQueryService;
import com.alpacaflow.meditrackplatform.monitoring.infrastructure.persistence.jpa.repositories.MonitoredDeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

/** A device without measurements yet returns an empty list. */
@Service
public class MeasurementQueryServiceImpl implements MeasurementQueryService {

    private final MonitoredDeviceRepository monitoredDeviceRepository;

    public MeasurementQueryServiceImpl(MonitoredDeviceRepository monitoredDeviceRepository) {
        this.monitoredDeviceRepository = monitoredDeviceRepository;
    }

    private <T> List<T> measurementsOf(Long deviceId, Function<MonitoredDevice, List<T>> getter) {
        return monitoredDeviceRepository.findByDeviceId(deviceId).map(getter).orElse(List.of());
    }

    @Override
    public List<HeartRateMeasurement> handle(GetAllHeartRateMeasurementsByDeviceIdQuery query) {
        return measurementsOf(query.deviceId(), MonitoredDevice::getHeartRateMeasurements);
    }

    @Override
    public List<TemperatureMeasurement> handle(GetAllTemperatureMeasurementsByDeviceIdQuery query) {
        return measurementsOf(query.deviceId(), MonitoredDevice::getTemperatureMeasurements);
    }

    @Override
    public List<OxygenMeasurement> handle(GetAllOxygenMeasurementsByDeviceIdQuery query) {
        return measurementsOf(query.deviceId(), MonitoredDevice::getOxygenMeasurements);
    }

    @Override
    public List<BloodPressureMeasurement> handle(GetAllBloodPressureMeasurementsByDeviceIdQuery query) {
        return measurementsOf(query.deviceId(), MonitoredDevice::getBloodPressureMeasurements);
    }
}
