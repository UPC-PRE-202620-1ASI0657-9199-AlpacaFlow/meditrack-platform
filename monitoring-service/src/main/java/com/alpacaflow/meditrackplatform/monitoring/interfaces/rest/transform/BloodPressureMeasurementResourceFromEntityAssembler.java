package com.alpacaflow.meditrackplatform.monitoring.interfaces.rest.transform;

import com.alpacaflow.meditrackplatform.monitoring.domain.model.entities.BloodPressureMeasurement;
import com.alpacaflow.meditrackplatform.monitoring.interfaces.rest.resources.BloodPressureMeasurementResource;

/**
 * Assembler to convert BloodPressureMeasurement entity to BloodPressureMeasurementResource
 */
public class BloodPressureMeasurementResourceFromEntityAssembler {
    /**
     * Convert a BloodPressureMeasurement entity to a BloodPressureMeasurementResource
     * @param entity The entity to convert
     * @return The BloodPressureMeasurementResource
     */
    public static BloodPressureMeasurementResource toResourceFromEntity(BloodPressureMeasurement entity) {
        return new BloodPressureMeasurementResource(
                entity.getId(),
                entity.getDiastolic(),
                entity.getSystolic(),
                entity.getMeasuredAt().toString()
        );
    }
}

