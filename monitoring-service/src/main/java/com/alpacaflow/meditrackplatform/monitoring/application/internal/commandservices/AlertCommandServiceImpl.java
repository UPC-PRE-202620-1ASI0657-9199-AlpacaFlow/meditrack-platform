package com.alpacaflow.meditrackplatform.monitoring.application.internal.commandservices;

import com.alpacaflow.meditrackplatform.monitoring.application.internal.outboundservices.acl.ExternalNotificationService;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.aggregates.Alert;
import com.alpacaflow.meditrackplatform.monitoring.domain.model.commands.CreateAlertCommand;
import com.alpacaflow.meditrackplatform.monitoring.domain.services.AlertCommandService;
import com.alpacaflow.meditrackplatform.monitoring.infrastructure.persistence.jpa.repositories.AlertRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of the AlertCommandService interface.
 * <p>Saves the alert and then asks the Notification Service to notify it.</p>
 * @see AlertCommandService
 * @see AlertRepository
 * @see ExternalNotificationService
 */
@Service
public class AlertCommandServiceImpl implements AlertCommandService {
    private final AlertRepository alertRepository;
    private final ExternalNotificationService externalNotificationService;

    public AlertCommandServiceImpl(AlertRepository alertRepository,
                                   ExternalNotificationService externalNotificationService) {
        this.alertRepository = alertRepository;
        this.externalNotificationService = externalNotificationService;
    }

    @Override
    public Long handle(CreateAlertCommand command) {
        var alert = new Alert(command);
        try {
            alertRepository.save(alert);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving alert: %s".formatted(e.getMessage()));
        }
        externalNotificationService.notifyAlert(alert);
        return alert.getId();
    }
}
