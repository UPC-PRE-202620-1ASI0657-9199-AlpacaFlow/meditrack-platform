package com.alpacaflow.meditrackplatform.notifications.domain.exceptions;

public class NotificationNotFoundException extends RuntimeException {
    public NotificationNotFoundException(Long notificationId) {
        super("Notification with id %d not found".formatted(notificationId));
    }
}
