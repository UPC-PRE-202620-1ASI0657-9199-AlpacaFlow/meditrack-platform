package com.alpacaflow.meditrackplatform.notifications.domain;

import com.alpacaflow.meditrackplatform.notifications.domain.model.aggregates.Notification;
import com.alpacaflow.meditrackplatform.notifications.domain.model.commands.CreateNotificationCommand;
import com.alpacaflow.meditrackplatform.notifications.domain.model.valueobjects.ENotificationSeverity;
import com.alpacaflow.meditrackplatform.notifications.domain.model.valueobjects.ENotificationStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Domain rules of the Notification aggregate (no database or Spring context needed). */
class NotificationTests {

    private static CreateNotificationCommand dangerAlert() {
        return new CreateNotificationCommand(1L, 1L, "DANGER", "Abnormal heart rate recorded: 145 bpm");
    }

    @Test
    void newNotificationIsUnreadAndKeepsTheAlertData() {
        var notification = new Notification(dangerAlert());

        assertEquals(ENotificationStatus.UNREAD, notification.getStatus());
        assertFalse(notification.isRead());
        assertNull(notification.getReadAt());
        assertNotNull(notification.getSentAt());
        assertEquals(1L, notification.getAlertId());
        assertEquals(1L, notification.getDeviceId());
        assertEquals("Abnormal heart rate recorded: 145 bpm", notification.getMessage());
    }

    @Test
    void severityDefinesTheTitle() {
        var danger = new Notification(dangerAlert());
        var warning = new Notification(new CreateNotificationCommand(2L, 1L, "warning", "Low oxygen saturation recorded: 91%"));

        assertEquals(ENotificationSeverity.DANGER, danger.getSeverity());
        assertEquals("Critical vital sign alert", danger.getTitle());
        assertEquals(ENotificationSeverity.WARNING, warning.getSeverity());
        assertEquals("Vital sign warning", warning.getTitle());
    }

    @Test
    void markAsReadChangesStatusAndSetsReadTime() {
        var notification = new Notification(dangerAlert());

        notification.markAsRead();

        assertTrue(notification.isRead());
        assertEquals(ENotificationStatus.READ, notification.getStatus());
        assertNotNull(notification.getReadAt());
    }

    @Test
    void markingAsReadTwiceKeepsTheFirstReadTime() {
        var notification = new Notification(dangerAlert());
        notification.markAsRead();
        var firstReadAt = notification.getReadAt();

        notification.markAsRead();

        assertEquals(firstReadAt, notification.getReadAt());
    }

    @Test
    void rejectsUnknownSeverity() {
        var command = new CreateNotificationCommand(1L, 1L, "CRITICAL", "Message");
        assertThrows(IllegalArgumentException.class, () -> new Notification(command));
    }

    @Test
    void rejectsInvalidCommandData() {
        assertThrows(IllegalArgumentException.class, () -> new CreateNotificationCommand(0L, 1L, "DANGER", "Message"));
        assertThrows(IllegalArgumentException.class, () -> new CreateNotificationCommand(1L, null, "DANGER", "Message"));
        assertThrows(IllegalArgumentException.class, () -> new CreateNotificationCommand(1L, 1L, " ", "Message"));
        assertThrows(IllegalArgumentException.class, () -> new CreateNotificationCommand(1L, 1L, "DANGER", ""));
    }
}
