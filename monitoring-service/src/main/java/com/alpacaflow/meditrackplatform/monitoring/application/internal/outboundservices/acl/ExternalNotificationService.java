package com.alpacaflow.meditrackplatform.monitoring.application.internal.outboundservices.acl;

import com.alpacaflow.meditrackplatform.monitoring.domain.model.aggregates.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * Anti-corruption layer towards the Notification Service.
 * <p>When an alert is created, the Monitoring Service asks the Notification Service to
 * notify it ({@code POST /api/v1/notifications}), forwarding the caller's JWT. The call is
 * best-effort: if the Notification Service is down, the measurement and the alert are still
 * saved and the failure is logged, so monitoring never stops because of notifications.</p>
 */
@Service
public class ExternalNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalNotificationService.class);

    private final RestClient restClient;

    public ExternalNotificationService(@Value("${services.notifications.url}") String notificationsServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(notificationsServiceUrl).build();
    }

    /** @return true if the Notification Service accepted the notification */
    public boolean notifyAlert(Alert alert) {
        var body = Map.of(
                "alertId", alert.getId(),
                "deviceId", alert.getDeviceId(),
                "severity", alert.getAlertType().toString(),
                "message", alert.getMessage());
        try {
            restClient.post()
                    .uri("/api/v1/notifications")
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(this::forwardAuthorization)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException e) {
            LOGGER.warn("Alert {} saved but not notified: {}", alert.getId(), e.getMessage());
            return false;
        }
    }

    private void forwardAuthorization(HttpHeaders headers) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null) headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
    }
}
