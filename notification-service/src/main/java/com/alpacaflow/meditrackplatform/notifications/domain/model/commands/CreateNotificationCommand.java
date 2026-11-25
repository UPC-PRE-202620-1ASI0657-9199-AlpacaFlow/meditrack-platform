package com.alpacaflow.meditrackplatform.notifications.domain.model.commands;

/**
 * Command to create a notification for an alert raised by the Monitoring Service.
 * @param alertId Id of the alert in the Monitoring Service
 * @param deviceId Id of the smart patch that produced the alert
 * @param severity Alert type (DANGER or WARNING)
 * @param message Alert message
 */
public record CreateNotificationCommand(Long alertId, Long deviceId, String severity, String message) {
    public CreateNotificationCommand {
        if (alertId == null || alertId <= 0) throw new IllegalArgumentException("Alert ID must be a positive number");
        if (deviceId == null || deviceId <= 0) throw new IllegalArgumentException("Device ID must be a positive number");
        if (severity == null || severity.isBlank()) throw new IllegalArgumentException("Severity is required");
        if (message == null || message.isBlank()) throw new IllegalArgumentException("Message is required");
    }
}
