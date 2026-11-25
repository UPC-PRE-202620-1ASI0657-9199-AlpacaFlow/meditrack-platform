package com.alpacaflow.meditrackplatform.notifications.application.internal.queryservices;

import com.alpacaflow.meditrackplatform.notifications.domain.model.aggregates.Notification;
import com.alpacaflow.meditrackplatform.notifications.domain.model.queries.GetAllNotificationsByDeviceIdQuery;
import com.alpacaflow.meditrackplatform.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.alpacaflow.meditrackplatform.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.alpacaflow.meditrackplatform.notifications.domain.services.NotificationQueryService;
import com.alpacaflow.meditrackplatform.notifications.infrastructure.persistence.jpa.repositories.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public List<Notification> handle(GetAllNotificationsQuery query) {
        return notificationRepository.findAllByOrderBySentAtDesc();
    }

    @Override
    public List<Notification> handle(GetAllNotificationsByDeviceIdQuery query) {
        return notificationRepository.findAllByDeviceIdOrderBySentAtDesc(query.deviceId());
    }

    @Override
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(query.notificationId());
    }
}
