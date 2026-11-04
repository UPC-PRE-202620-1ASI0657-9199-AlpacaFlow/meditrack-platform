package com.alpacaflow.meditrackplatform.monitoring.domain.services;

import com.alpacaflow.meditrackplatform.monitoring.domain.model.commands.*;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.entities.*;

public interface MeasurementCommandService {
    HeartRateMeasurement handle(AddHeartRateMeasurementToDeviceCommand command);

    TemperatureMeasurement handle(AddTemperatureMeasurementToDeviceCommand command);

    OxygenMeasurement handle(AddOxygenMeasurementToDeviceCommand command);

    BloodPressureMeasurement handle(AddBloodPressureMeasurementToDeviceCommand command);
}
