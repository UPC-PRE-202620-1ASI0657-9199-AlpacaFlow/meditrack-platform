package com.alpacaflow.meditrackplatform.notifications.domain.model.commands;

/**
 * Command to mark a notification as read.
 * @param notificationId Id of the notification
 */
public record MarkNotificationAsReadCommand(Long notificationId) {
}
