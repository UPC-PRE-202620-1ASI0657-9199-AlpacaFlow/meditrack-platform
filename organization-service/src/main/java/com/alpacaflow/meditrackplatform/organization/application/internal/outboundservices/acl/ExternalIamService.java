package com.alpacaflow.meditrackplatform.organization.application.internal.outboundservices.acl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;

/**
 * Anti-corruption layer towards the IAM Service.
 * <p>Replaces the in-memory calls to the IAM UserCommandService used in the monolith
 * with HTTP calls to the IAM Service. The caller's JWT is forwarded.</p>
 */
@Service
public class ExternalIamService {

    private final RestClient restClient;

    public ExternalIamService(@Value("${services.iam.url}") String iamServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(iamServiceUrl).build();
    }

    public Optional<ExternalUser> fetchUserById(Long userId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/api/v1/users/{userId}", userId)
                    .headers(this::forwardAuthorization)
                    .retrieve()
                    .body(ExternalUser.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new IllegalStateException("IAM Service is not available: %s".formatted(e.getMessage()));
        }
    }

    public Optional<ExternalUser> fetchUserByEmail(String email) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/api/v1/users/email/{email}", email)
                    .headers(this::forwardAuthorization)
                    .retrieve()
                    .body(ExternalUser.class));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new IllegalStateException("IAM Service is not available: %s".formatted(e.getMessage()));
        }
    }

    /**
     * Creates a user in the IAM Service (used when an admin registers a doctor or caregiver).
     */
    public ExternalUser createUser(String email, String role) {
        try {
            var user = restClient.post()
                    .uri("/api/v1/users")
                    .headers(this::forwardAuthorization)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("email", email, "role", role))
                    .retrieve()
                    .body(ExternalUser.class);
            if (user == null || user.userId() == null)
                throw new IllegalStateException("IAM Service returned no user id");
            return user;
        } catch (RestClientException e) {
            throw new IllegalStateException("IAM Service could not create the user: %s".formatted(e.getMessage()));
        }
    }

    private void forwardAuthorization(HttpHeaders headers) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null) headers.set(HttpHeaders.AUTHORIZATION, authorization);
        }
    }
}
