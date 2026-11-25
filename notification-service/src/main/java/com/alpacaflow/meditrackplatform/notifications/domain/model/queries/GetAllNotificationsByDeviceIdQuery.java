package com.alpacaflow.meditrackplatform.notifications.domain.model.queries;

/** Query to get the notifications of one smart patch, newest first. */
public record GetAllNotificationsByDeviceIdQuery(Long deviceId) {
}
