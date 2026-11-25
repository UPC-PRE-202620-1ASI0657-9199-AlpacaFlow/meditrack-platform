package com.alpacaflow.meditrackplatform.notifications.application.internal.commandservices;

import com.alpacaflow.meditrackplatform.notifications.domain.exceptions.NotificationNotFoundException;
import com.alpacaflow.meditrackplatform.notifications.domain.model.aggregates.Notification;
import com.alpacaflow.meditrackplatform.notifications.domain.model.commands.CreateNotificationCommand;
import com.alpacaflow.meditrackplatform.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.alpacaflow.meditrackplatform.notifications.domain.services.NotificationCommandService;
import com.alpacaflow.meditrackplatform.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationCommandServiceImpl implements NotificationCommandService {

    private final NotificationRepository notificationRepository;

    public NotificationCommandServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Idempotent: if the Monitoring Service retries the call for the same alert,
     * the existing notification is returned instead of creating a duplicate.
     */
    @Override
    @Transactional
    public Notification handle(CreateNotificationCommand command) {
        return notificationRepository.findByAlertId(command.alertId())
                .orElseGet(() -> notificationRepository.save(new Notification(command)));
    }

    @Override
    @Transactional
    public Notification handle(MarkNotificationAsReadCommand command) {
        var notification = notificationRepository.findById(command.notificationId())
                .orElseThrow(() -> new NotificationNotFoundException(command.notificationId()));
        notification.markAsRead();
        return notificationRepository.save(notification);
    }
}
