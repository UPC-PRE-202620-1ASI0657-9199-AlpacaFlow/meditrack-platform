package com.alpacaflow.meditrackplatform.notifications.domain.model.valueobjects;

/**
 * Lifecycle of a notification: it is created as UNREAD and becomes READ when the user opens it.
 */
public enum ENotificationStatus {
    UNREAD,
    READ
}
