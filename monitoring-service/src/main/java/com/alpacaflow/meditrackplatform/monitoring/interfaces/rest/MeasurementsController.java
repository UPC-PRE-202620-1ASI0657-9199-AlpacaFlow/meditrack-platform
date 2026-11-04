package com.alpacaflow.meditrackplatform.monitoring.interfaces.rest;

import com.alpacaflow.meditrackplatform.monitoring.domain.model.queries.*;
import com.alpacaflow.meditrackplatform.monitoring.domain.services.MeasurementCommandService;
import com.alpacaflow.meditrackplatform.monitoring.domain.services.MeasurementQueryService;
import com.alpacaflow.meditrackplatform.monitoring.interfaces.rest.resources.*;
import com.alpacaflow.meditrackplatform.monitoring.interfaces.rest.transform.*;
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
 * Vital sign measurements of a smart patch.
 * <p>The paths are the same ones devices-service exposed before the extraction, so the
 * frontend does not change: the API Gateway now routes them to this service.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/devices", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Measurements", description = "Vital sign measurement endpoints")
public class MeasurementsController {
    private final MeasurementCommandService measurementCommandService;
    private final MeasurementQueryService measurementQueryService;

    public MeasurementsController(MeasurementCommandService measurementCommandService,
                                  MeasurementQueryService measurementQueryService) {
        this.measurementCommandService = measurementCommandService;
        this.measurementQueryService = measurementQueryService;
    }

    @PostMapping("/{deviceId}/measurements/blood-pressure")
    @Operation(summary = "Register a blood pressure measurement",
               description = "Registers a blood pressure measurement for a smart patch. If the value is outside the safe range, an alert is created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Measurement registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Device not found in the Devices Service"),
            @ApiResponse(responseCode = "503", description = "Devices Service not available")})
    public ResponseEntity<BloodPressureMeasurementResource> addBloodPressureMeasurement(
            @PathVariable Long deviceId,
            @RequestBody AddBloodPressureMeasurementToDeviceResource resource) {
        var command = AddBloodPressureMeasurementToDeviceCommandFromResourceAssembler.toCommandFromResource(resource, deviceId);
        var measurement = measurementCommandService.handle(command);
        return new ResponseEntity<>(BloodPressureMeasurementResourceFromEntityAssembler.toResourceFromEntity(measurement), HttpStatus.CREATED);
    }

    @PostMapping("/{deviceId}/measurements/heart-rate")
    @Operation(summary = "Register a heart rate measurement",
               description = "Registers a heart rate measurement for a smart patch. If the value is outside the safe range, an alert is created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Measurement registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Device not found in the Devices Service"),
            @ApiResponse(responseCode = "503", description = "Devices Service not available")})
    public ResponseEntity<HeartRateMeasurementResource> addHeartRateMeasurement(
            @PathVariable Long deviceId,
            @RequestBody AddHeartRateMeasurementToDeviceResource resource) {
        var command = AddHeartRateMeasurementToDeviceCommandFromResourceAssembler.toCommandFromResource(resource, deviceId);
        var measurement = measurementCommandService.handle(command);
        return new ResponseEntity<>(HeartRateMeasurementResourceFromEntityAssembler.toResourceFromEntity(measurement), HttpStatus.CREATED);
    }

    @PostMapping("/{deviceId}/measurements/temperature")
    @Operation(summary = "Register a temperature measurement",
               description = "Registers a temperature measurement for a smart patch. If the value is outside the safe range, an alert is created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Measurement registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Device not found in the Devices Service"),
            @ApiResponse(responseCode = "503", description = "Devices Service not available")})
    public ResponseEntity<TemperatureMeasurementResource> addTemperatureMeasurement(
            @PathVariable Long deviceId,
            @RequestBody AddTemperatureMeasurementToDeviceResource resource) {
        var command = AddTemperatureMeasurementToDeviceCommandFromResourceAssembler.toCommandFromResource(resource, deviceId);
        var measurement = measurementCommandService.handle(command);
        return new ResponseEntity<>(TemperatureMeasurementResourceFromEntityAssembler.toResourceFromEntity(measurement), HttpStatus.CREATED);
    }

    @PostMapping("/{deviceId}/measurements/oxygen")
    @Operation(summary = "Register an oxygen saturation measurement",
               description = "Registers an oxygen saturation measurement for a smart patch. If the value is outside the safe range, an alert is created.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Measurement registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Device not found in the Devices Service"),
            @ApiResponse(responseCode = "503", description = "Devices Service not available")})
    public ResponseEntity<OxygenMeasurementResource> addOxygenMeasurement(
            @PathVariable Long deviceId,
            @RequestBody AddOxygenMeasurementToDeviceResource resource) {
        var command = AddOxygenMeasurementToDeviceCommandFromResourceAssembler.toCommandFromResource(resource, deviceId);
        var measurement = measurementCommandService.handle(command);
        return new ResponseEntity<>(OxygenMeasurementResourceFromEntityAssembler.toResourceFromEntity(measurement), HttpStatus.CREATED);
    }

    @GetMapping("/{deviceId}/measurements/blood-pressure")
    @Operation(summary = "Get blood pressure measurements of a device",
               description = "Returns the latest measurements of this type (empty list if there are none)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Measurements found")})
    public ResponseEntity<List<BloodPressureMeasurementResource>> getAllBloodPressureMeasurements(@PathVariable Long deviceId) {
        var measurements = measurementQueryService.handle(new GetAllBloodPressureMeasurementsByDeviceIdQuery(deviceId));
        return ResponseEntity.ok(measurements.stream().map(BloodPressureMeasurementResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{deviceId}/measurements/heart-rate")
    @Operation(summary = "Get heart rate measurements of a device",
               description = "Returns the latest measurements of this type (empty list if there are none)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Measurements found")})
    public ResponseEntity<List<HeartRateMeasurementResource>> getAllHeartRateMeasurements(@PathVariable Long deviceId) {
        var measurements = measurementQueryService.handle(new GetAllHeartRateMeasurementsByDeviceIdQuery(deviceId));
        return ResponseEntity.ok(measurements.stream().map(HeartRateMeasurementResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{deviceId}/measurements/temperature")
    @Operation(summary = "Get temperature measurements of a device",
               description = "Returns the latest measurements of this type (empty list if there are none)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Measurements found")})
    public ResponseEntity<List<TemperatureMeasurementResource>> getAllTemperatureMeasurements(@PathVariable Long deviceId) {
        var measurements = measurementQueryService.handle(new GetAllTemperatureMeasurementsByDeviceIdQuery(deviceId));
        return ResponseEntity.ok(measurements.stream().map(TemperatureMeasurementResourceFromEntityAssembler::toResourceFromEntity).toList());
    }

    @GetMapping("/{deviceId}/measurements/oxygen")
    @Operation(summary = "Get oxygen saturation measurements of a device",
               description = "Returns the latest measurements of this type (empty list if there are none)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Measurements found")})
    public ResponseEntity<List<OxygenMeasurementResource>> getAllOxygenMeasurements(@PathVariable Long deviceId) {
        var measurements = measurementQueryService.handle(new GetAllOxygenMeasurementsByDeviceIdQuery(deviceId));
        return ResponseEntity.ok(measurements.stream().map(OxygenMeasurementResourceFromEntityAssembler::toResourceFromEntity).toList());
    }
}
