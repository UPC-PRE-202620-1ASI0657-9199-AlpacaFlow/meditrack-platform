package com.alpacaflow.meditrackplatform.notifications.domain.services;

import com.alpacaflow.meditrackplatform.notifications.domain.model.aggregates.Notification;
import com.alpacaflow.meditrackplatform.notifications.domain.model.queries.GetAllNotificationsByDeviceIdQuery;
import com.alpacaflow.meditrackplatform.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.alpacaflow.meditrackplatform.notifications.domain.model.queries.GetNotificationByIdQuery;

import java.util.List;
import java.util.Optional;

public interface NotificationQueryService {
    List<Notification> handle(GetAllNotificationsQuery query);

    List<Notification> handle(GetAllNotificationsByDeviceIdQuery query);

    Optional<Notification> handle(GetNotificationByIdQuery query);
}
