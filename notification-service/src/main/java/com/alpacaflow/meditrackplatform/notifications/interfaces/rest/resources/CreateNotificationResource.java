package com.alpacaflow.meditrackplatform.notifications.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Body sent by the Monitoring Service when it raises an alert.
 */
public record CreateNotificationResource(
        @NotNull(message = "alertId is required") @Positive Long alertId,
        @NotNull(message = "deviceId is required") @Positive Long deviceId,
        @NotBlank(message = "severity is required") String severity,
        @NotBlank(message = "message is required") String message
) {
}
