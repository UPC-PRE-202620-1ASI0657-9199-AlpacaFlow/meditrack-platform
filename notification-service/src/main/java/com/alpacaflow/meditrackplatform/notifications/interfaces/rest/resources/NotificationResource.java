package com.alpacaflow.meditrackplatform.notifications.interfaces.rest.resources;

public record NotificationResource(
        Long id,
        Long alertId,
        Long deviceId,
        String severity,
        String title,
        String message,
        String status,
        String sentAt,
        String readAt
) {
}
