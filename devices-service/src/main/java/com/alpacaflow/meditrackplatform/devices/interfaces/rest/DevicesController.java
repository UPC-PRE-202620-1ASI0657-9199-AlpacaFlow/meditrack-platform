package com.alpacaflow.meditrackplatform.devices.interfaces.rest;

import com.alpacaflow.meditrackplatform.devices.domain.model.queries.*;
import com.alpacaflow.meditrackplatform.devices.domain.services.DeviceCommandService;
import com.alpacaflow.meditrackplatform.devices.domain.services.DeviceQueryService;
import com.alpacaflow.meditrackplatform.devices.interfaces.rest.resources.*;
import com.alpacaflow.meditrackplatform.devices.interfaces.rest.transform.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * DevicesController
 * <p>
 *     All device-related endpoints.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/devices", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Devices", description = "Available Device Endpoints")
public class DevicesController {
    private final DeviceCommandService deviceCommandService;
    private final DeviceQueryService deviceQueryService;

    public DevicesController(DeviceCommandService deviceCommandService, DeviceQueryService deviceQueryService) {
        this.deviceCommandService = deviceCommandService;
        this.deviceQueryService = deviceQueryService;
    }

    /**
     * Create a new device
     * @param resource The {@link CreateDeviceResource} instance
     * @return The {@link DeviceResource} resource for the created device
     */
    @PostMapping
    @Operation(summary = "Create a new device", description = "Create a new device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Device not found")})
    public ResponseEntity<DeviceResource> createDevice(@RequestBody CreateDeviceResource resource) {
        var createDeviceCommand = CreateDeviceCommandFromResourceAssembler.toCommandFromResource(resource);
        var deviceId = deviceCommandService.handle(createDeviceCommand);
        if (deviceId == null || deviceId == 0L) return ResponseEntity.badRequest().build();
        var getDeviceByIdQuery = new GetDeviceByIdQuery(deviceId);
        var device = deviceQueryService.handle(getDeviceByIdQuery);
        if (device.isEmpty()) return ResponseEntity.notFound().build();
        var deviceEntity = device.get();
        var deviceResource = DeviceResourceFromEntityAssembler.toResourceFromEntity(deviceEntity);
        return new ResponseEntity<>(deviceResource, HttpStatus.CREATED);
    }

    /**
     * Get device by id
     * @param deviceId The device id
     * @return The {@link DeviceResource} resource for the device
     */
    @GetMapping("/{deviceId}")
    @Operation(summary = "Get device by id", description = "Get device by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(responseCode = "404", description = "Device not found")})
    public ResponseEntity<DeviceResource> getDeviceById(@PathVariable Long deviceId) {
        var getDeviceByIdQuery = new GetDeviceByIdQuery(deviceId);
        var device = deviceQueryService.handle(getDeviceByIdQuery);
        if (device.isEmpty()) return ResponseEntity.notFound().build();
        var deviceEntity = device.get();
        var deviceResource = DeviceResourceFromEntityAssembler.toResourceFromEntity(deviceEntity);
        return ResponseEntity.ok(deviceResource);
    }

    /**
     * Get all devices
     * @return The list of {@link DeviceResource} resources for all devices
     */
    @GetMapping
    @Operation(summary = "Get all devices", description = "Get all devices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devices found")})
    public ResponseEntity<List<DeviceResource>> getAllDevices() {
        var getAllDevicesQuery = new GetAllDevicesQuery();
        var devices = deviceQueryService.handle(getAllDevicesQuery);
        var deviceResources = devices.stream()
                .map(DeviceResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(deviceResources);
    }
}
