package com.alpacaflow.meditrackplatform.notifications.domain.services;

import com.alpacaflow.meditrackplatform.notifications.domain.model.aggregates.Notification;
import com.alpacaflow.meditrackplatform.notifications.domain.model.commands.CreateNotificationCommand;
import com.alpacaflow.meditrackplatform.notifications.domain.model.commands.MarkNotificationAsReadCommand;

public interface NotificationCommandService {
    /** Creates the notification of an alert, or returns the existing one if the alert was already notified. */
    Notification handle(CreateNotificationCommand command);

    Notification handle(MarkNotificationAsReadCommand command);
}
