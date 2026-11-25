package com.alpacaflow.meditrackplatform.notifications.interfaces.rest.transform;

import com.alpacaflow.meditrackplatform.notifications.domain.model.commands.CreateNotificationCommand;
import com.alpacaflow.meditrackplatform.notifications.interfaces.rest.resources.CreateNotificationResource;

public class CreateNotificationCommandFromResourceAssembler {
    public static CreateNotificationCommand toCommandFromResource(CreateNotificationResource resource) {
        return new CreateNotificationCommand(resource.alertId(), resource.deviceId(), resource.severity(), resource.message());
    }
}
