package com.alpacaflow.meditrackplatform.shared.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Validates JWT tokens issued by the IAM Service.
 * <p>This service does not issue tokens nor store users: it only verifies the
 * signature with the shared secret and reads the subject (user email).</p>
 */
@Component
public class JwtTokenValidator {

    @Value("${authorization.jwt.secret}")
    private String secret;

    public Optional<String> getSubjectIfValid(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token).getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
