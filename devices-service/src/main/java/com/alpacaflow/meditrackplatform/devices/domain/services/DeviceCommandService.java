package com.alpacaflow.meditrackplatform.devices.domain.services;

import com.alpacaflow.meditrackplatform.devices.domain.model.commands.*;

/**
 * DeviceCommandService
 * Service that handles device commands
 */
public interface DeviceCommandService {
    /**
     * Handle a create device command
     * @param command The create device command containing the device data
     * @return The ID of the created device
     * @see CreateDeviceCommand
     */
    Long handle(CreateDeviceCommand command);
}
