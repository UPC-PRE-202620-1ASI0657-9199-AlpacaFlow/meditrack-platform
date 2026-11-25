package com.alpacaflow.meditrackplatform.notifications.interfaces.rest.transform;

import com.alpacaflow.meditrackplatform.notifications.domain.model.aggregates.Notification;
import com.alpacaflow.meditrackplatform.notifications.interfaces.rest.resources.NotificationResource;

public class NotificationResourceFromEntityAssembler {
    public static NotificationResource toResourceFromEntity(Notification entity) {
        return new NotificationResource(
                entity.getId(),
                entity.getAlertId(),
                entity.getDeviceId(),
                entity.getSeverity().toString(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getStatus().toString(),
                entity.getSentAt().toString(),
                entity.getReadAt() == null ? null : entity.getReadAt().toString()
        );
    }
}
