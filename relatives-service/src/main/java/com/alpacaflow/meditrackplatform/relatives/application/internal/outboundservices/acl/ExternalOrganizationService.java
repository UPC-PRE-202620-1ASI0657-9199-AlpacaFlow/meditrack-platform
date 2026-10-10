package com.alpacaflow.meditrackplatform.relatives.application.internal.outboundservices.acl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;

/**
 * Anti-corruption layer towards the Organization Service.
 * <p>Replaces the in-memory call to SeniorCitizenQueryService used in the monolith
 * with HTTP calls. The caller's JWT is forwarded so the Organization Service can
 * authorize the request.</p>
 */
@Service
public class ExternalOrganizationService {

    private final RestClient restClient;

    public ExternalOrganizationService(@Value("${services.organization.url}") String organizationServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(organizationServiceUrl).build();
    }

    public Optional<ExternalSeniorCitizen> fetchSeniorCitizenById(Long seniorCitizenId) {
        try {
            var seniorCitizen = restClient.get()
                    .uri("/api/v1/senior-citizens/{id}", seniorCitizenId)
                    .headers(this::forwardAuthorization)
                    .retrieve()
                    .body(ExternalSeniorCitizen.class);
            return Optional.ofNullable(seniorCitizen);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new IllegalStateException("Organization Service is not available: %s".formatted(e.getMessage()));
        }
    }

    @SuppressWarnings("unchecked")
    public Optional<String> fetchOrganizationTypeById(Long organizationId) {
        try {
            Map<String, Object> organization = restClient.get()
                    .uri("/api/v1/organizations/{id}", organizationId)
                    .headers(this::forwardAuthorization)
                    .retrieve()
                    .body(Map.class);
            return Optional.ofNullable(organization).map(o -> (String) o.get("type"));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new IllegalStateException("Organization Service is not available: %s".formatted(e.getMessage()));
        }
    }

    private void forwardAuthorization(HttpHeaders headers) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null) headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
    }
}
