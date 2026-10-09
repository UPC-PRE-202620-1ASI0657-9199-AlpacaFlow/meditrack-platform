package com.alpacaflow.meditrackplatform.iam.application.internal.outboundservices.acl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Anti-corruption layer towards the Organization bounded context.
 * <p>In the monolith, IAM invoked the Organization command services in memory.
 * After extracting IAM as an independent microservice, that dependency is replaced
 * by HTTP calls to the Organization Service REST API.</p>
 */
@Service
public class ExternalOrganizationService {

    private final RestClient restClient;

    public ExternalOrganizationService(@Value("${services.organization.url}") String organizationServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(organizationServiceUrl)
                .build();
    }

    /**
     * Creates an organization in the Organization Service.
     * @param name the organization name
     * @param type the organization type ("clinic" or "resident")
     * @return the id of the created organization
     */
    public Long createOrganization(String name, String type) {
        try {
            var response = restClient.post()
                    .uri("/api/v1/organizations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("name", name, "type", type))
                    .retrieve()
                    .body(OrganizationResponse.class);
            if (response == null || response.id() == null)
                throw new IllegalStateException("Organization Service returned no organization id");
            return response.id();
        } catch (RestClientException e) {
            throw new IllegalStateException("Organization Service is not available: " + e.getMessage(), e);
        }
    }

    /**
     * Creates the administrator of an organization in the Organization Service.
     * @param organizationId the organization id
     * @param userId the IAM user id of the administrator
     * @param firstName the administrator first name
     * @param lastName the administrator last name
     */
    public void createAdmin(Long organizationId, Long userId, String firstName, String lastName) {
        try {
            restClient.post()
                    .uri("/api/v1/admins")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "organizationId", organizationId,
                            "userId", userId,
                            "firstName", firstName,
                            "lastName", lastName))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new IllegalStateException("Organization Service is not available: " + e.getMessage(), e);
        }
    }

    private record OrganizationResponse(Long id, String name, String type) {
    }
}
