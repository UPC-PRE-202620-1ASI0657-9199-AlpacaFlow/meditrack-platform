package com.alpacaflow.meditrackplatform.devices.domain.services;

import com.alpacaflow.meditrackplatform.devices.domain.model.aggregates.Device;
import com.alpacaflow.meditrackplatform.devices.domain.model.queries.*;

import java.util.List;
import java.util.Optional;

/**
 * DeviceQueryService
 * Service that handles device queries
 */
public interface DeviceQueryService {
    /**
     * Handle a get device by id query
     * @param query The query containing the device id
     * @return The device if found
     * @see GetDeviceByIdQuery
     */
    Optional<Device> handle(GetDeviceByIdQuery query);

    /**
     * Handle a get all devices query
     * @param query The query
     * @return List of all devices
     * @see GetAllDevicesQuery
     */
    List<Device> handle(GetAllDevicesQuery query);
}
