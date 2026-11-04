package com.alpacaflow.meditrackplatform.monitoring.interfaces.rest.resources;

/**
 * Resource for adding a heart rate measurement to a device
 * @param bpm The heart rate in beats per minute
 */
public record AddHeartRateMeasurementToDeviceResource(int bpm) {
}

