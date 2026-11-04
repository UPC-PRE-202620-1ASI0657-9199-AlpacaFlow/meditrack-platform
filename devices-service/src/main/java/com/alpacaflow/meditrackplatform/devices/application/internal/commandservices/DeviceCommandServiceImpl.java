package com.alpacaflow.meditrackplatform.devices.application.internal.commandservices;

import com.alpacaflow.meditrackplatform.devices.domain.model.aggregates.Device;
import com.alpacaflow.meditrackplatform.devices.domain.model.commands.*;
import com.alpacaflow.meditrackplatform.devices.domain.services.DeviceCommandService;
import com.alpacaflow.meditrackplatform.devices.infrastructure.persistence.jpa.repositories.DeviceRepository;
import org.springframework.stereotype.Service;

/**
 * Implementation of the DeviceCommandService interface.
 * <p>This class is responsible for handling the commands related to the Device aggregate. It requires a DeviceRepository.</p>
 * @see DeviceCommandService
 * @see DeviceRepository
 */
@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {
    private final DeviceRepository deviceRepository;

    public DeviceCommandServiceImpl(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Long handle(CreateDeviceCommand command) {
        var device = new Device(command);
        try {
            deviceRepository.save(device);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error saving device: %s".formatted(e.getMessage()));
        }
        return device.getId();
    }
}
