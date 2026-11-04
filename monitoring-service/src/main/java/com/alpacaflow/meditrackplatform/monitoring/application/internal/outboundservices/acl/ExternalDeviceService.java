package com.alpacaflow.meditrackplatform.monitoring.application.internal.outboundservices.acl;

import com.alpacaflow.meditrackplatform.monitoring.domain.exceptions.DevicesServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Anti-corruption layer towards the Devices Service.
 * <p>In the monolith, measurements were stored inside the Device aggregate. Now the
 * Monitoring Service only asks the Devices Service whether a device exists, over HTTP,
 * forwarding the caller's JWT so the Devices Service can authorize the request.</p>
 */
@Service
public class ExternalDeviceService {

    private final RestClient restClient;

    public ExternalDeviceService(@Value("${services.devices.url}") String devicesServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(devicesServiceUrl).build();
    }

    /**
     * @return true if the device exists, false if the Devices Service answers 404
     * @throws DevicesServiceUnavailableException if the Devices Service cannot be reached
     */
    public boolean existsDeviceById(Long deviceId) {
        try {
            restClient.get()
                    .uri("/api/v1/devices/{id}", deviceId)
                    .headers(this::forwardAuthorization)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (RestClientException e) {
            throw new DevicesServiceUnavailableException(e.getMessage());
        }
    }

    private void forwardAuthorization(HttpHeaders headers) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null) headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
    }
}
