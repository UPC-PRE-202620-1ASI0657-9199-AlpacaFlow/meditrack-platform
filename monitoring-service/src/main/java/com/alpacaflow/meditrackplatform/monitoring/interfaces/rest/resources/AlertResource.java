package com.alpacaflow.meditrackplatform.monitoring.interfaces.rest.resources;

/**
 * Resource representing an alert
 */
public record AlertResource(
        Long id,
        Long deviceId,
        String alertType,
        String message,
        double dataRegistered,
        String registeredAt
) {
}

