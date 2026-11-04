package com.alpacaflow.meditrackplatform.monitoring.interfaces.rest.resources;

/**
 * Resource representing a temperature measurement
 */
public record TemperatureMeasurementResource(
        Long id,
        double celsius,
        String measuredAt
) {
}

