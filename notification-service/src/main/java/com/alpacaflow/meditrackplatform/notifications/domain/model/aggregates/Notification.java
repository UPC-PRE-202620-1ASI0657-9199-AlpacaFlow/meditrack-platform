package com.alpacaflow.meditrackplatform.notifications.domain.model.aggregates;

import com.alpacaflow.meditrackplatform.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import com.alpacaflow.meditrackplatform.notifications.domain.model.commands.CreateNotificationCommand;
import com.alpacaflow.meditrackplatform.notifications.domain.model.valueobjects.ENotificationSeverity;
import com.alpacaflow.meditrackplatform.notifications.domain.model.valueobjects.ENotificationStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Notification aggregate root.
 * <p>Represents the notice a user receives when the Monitoring Service raises an alert.
 * The alert and the smart patch belong to other bounded contexts, so this aggregate only
 * keeps their identifiers (no foreign keys): the Notification Service never reads another
 * service's database. Each alert produces at most one notification.</p>
 */
@Getter
@Entity
public class Notification extends AuditableAbstractAggregateRoot<Notification> {

    @Column(nullable = false, unique = true)
    private Long alertId;

    @Column(nullable = false)
    private Long deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ENotificationSeverity severity;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ENotificationStatus status;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private LocalDateTime readAt;

    protected Notification() {
    }

    public Notification(CreateNotificationCommand command) {
        this.alertId = command.alertId();
        this.deviceId = command.deviceId();
        this.severity = parseSeverity(command.severity());
        this.title = this.severity == ENotificationSeverity.DANGER
                ? "Critical vital sign alert"
                : "Vital sign warning";
        this.message = command.message();
        this.status = ENotificationStatus.UNREAD;
        this.sentAt = LocalDateTime.now();
    }

    /** Marks the notification as read. Reading it again keeps the first read time. */
    public void markAsRead() {
        if (isRead()) return;
        this.status = ENotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }

    public boolean isRead() {
        return this.status == ENotificationStatus.READ;
    }

    private static ENotificationSeverity parseSeverity(String severity) {
        try {
            return ENotificationSeverity.valueOf(severity.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown severity: %s".formatted(severity));
        }
    }
}
