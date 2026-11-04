package com.alpacaflow.meditrackplatform;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks that the gateway blocks protected routes without a valid JWT and lets public routes through.
 * <p>The microservices are not running during the test, so a request that passes the filter ends in
 * a 5xx from the routing layer; what matters is that it is NOT rejected with 401.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "services.iam.url=http://localhost:59991",
                "services.organization.url=http://localhost:59992",
                "services.devices.url=http://localhost:59993",
                "services.relatives.url=http://localhost:59994"
        })
@AutoConfigureWebTestClient
class JwtAuthenticationGlobalFilterTests {

    @Autowired
    private WebTestClient client;

    @Value("${authorization.jwt.secret}")
    private String secret;

    @Test
    void protectedRouteWithoutTokenIsRejected() {
        client.get().uri("/api/v1/devices").exchange()
                .expectStatus().isUnauthorized()
                .expectBody().jsonPath("$.message").isEqualTo("Missing bearer token");
    }

    @Test
    void protectedRouteWithInvalidTokenIsRejected() {
        client.get().uri("/api/v1/organizations")
                .header("Authorization", "Bearer not-a-valid-token")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody().jsonPath("$.message").isEqualTo("Invalid or expired token");
    }

    @Test
    void protectedRouteWithValidTokenIsRouted() {
        String token = Jwts.builder()
                .subject("doctor@meditrack.com")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        var status = client.get().uri("/api/v1/devices")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .returnResult(String.class).getStatus();

        assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void signInIsPublic() {
        var status = client.post().uri("/api/v1/authentication/sign-in").exchange()
                .returnResult(String.class).getStatus();

        assertThat(status).isNotEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void healthIsPublic() {
        client.get().uri("/actuator/health").exchange().expectStatus().isOk();
    }
}
