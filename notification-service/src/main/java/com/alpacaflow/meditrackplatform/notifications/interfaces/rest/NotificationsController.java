package com.alpacaflow.meditrackplatform.notifications.interfaces.rest;

import com.alpacaflow.meditrackplatform.notifications.domain.model.commands.MarkNotificationAsReadCommand;
import com.alpacaflow.meditrackplatform.notifications.domain.model.queries.GetAllNotificationsByDeviceIdQuery;
import com.alpacaflow.meditrackplatform.notifications.domain.model.queries.GetAllNotificationsQuery;
import com.alpacaflow.meditrackplatform.notifications.domain.model.queries.GetNotificationByIdQuery;
import com.alpacaflow.meditrackplatform.notifications.domain.services.NotificationCommandService;
import com.alpacaflow.meditrackplatform.notifications.domain.services.NotificationQueryService;
import com.alpacaflow.meditrackplatform.notifications.interfaces.rest.resources.CreateNotificationResource;
import com.alpacaflow.meditrackplatform.notifications.interfaces.rest.resources.NotificationResource;
import com.alpacaflow.meditrackplatform.notifications.interfaces.rest.transform.CreateNotificationCommandFromResourceAssembler;
import com.alpacaflow.meditrackplatform.notifications.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * NotificationsController
 * <p>Notifications created from the alerts of the Monitoring Service.</p>
 */
@RestController
@RequestMapping(value = "/api/v1/notifications", produces = APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Available Notification Endpoints")
public class NotificationsController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    public NotificationsController(NotificationCommandService notificationCommandService,
                                   NotificationQueryService notificationQueryService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @PostMapping
    @Operation(summary = "Create notification",
            description = "Called by the Monitoring Service when an alert is raised. Idempotent per alertId.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")})
    public ResponseEntity<NotificationResource> createNotification(@Valid @RequestBody CreateNotificationResource resource) {
        var command = CreateNotificationCommandFromResourceAssembler.toCommandFromResource(resource);
        var notification = notificationCommandService.handle(command);
        return new ResponseEntity<>(NotificationResourceFromEntityAssembler.toResourceFromEntity(notification), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get notifications", description = "Get all notifications, newest first. Filter by smart patch with deviceId.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Notifications found")})
    public ResponseEntity<List<NotificationResource>> getNotifications(
            @Parameter(description = "Smart patch id") @RequestParam(required = false) Long deviceId) {
        var notifications = deviceId == null
                ? notificationQueryService.handle(new GetAllNotificationsQuery())
                : notificationQueryService.handle(new GetAllNotificationsByDeviceIdQuery(deviceId));
        return ResponseEntity.ok(notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .toList());
    }

    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by id", description = "Get notification by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification found"),
            @ApiResponse(responseCode = "404", description = "Notification not found")})
    public ResponseEntity<NotificationResource> getNotificationById(@PathVariable Long notificationId) {
        return notificationQueryService.handle(new GetNotificationByIdQuery(notificationId))
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "Mark notification as read", description = "Marks the notification as read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read"),
            @ApiResponse(responseCode = "404", description = "Notification not found")})
    public ResponseEntity<NotificationResource> markAsRead(@PathVariable Long notificationId) {
        var notification = notificationCommandService.handle(new MarkNotificationAsReadCommand(notificationId));
        return ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(notification));
    }
}
