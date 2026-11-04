package com.alpacaflow.meditrackplatform.monitoring.domain.services;

import com.alpacaflow.meditrackplatform.monitoring.domain.model.entities.*;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.queries.*;

import java.util.List;

public interface MeasurementQueryService {
    List<HeartRateMeasurement> handle(GetAllHeartRateMeasurementsByDeviceIdQuery query);

    List<TemperatureMeasurement> handle(GetAllTemperatureMeasurementsByDeviceIdQuery query);

    List<OxygenMeasurement> handle(GetAllOxygenMeasurementsByDeviceIdQuery query);

    List<BloodPressureMeasurement> handle(GetAllBloodPressureMeasurementsByDeviceIdQuery query);
}
