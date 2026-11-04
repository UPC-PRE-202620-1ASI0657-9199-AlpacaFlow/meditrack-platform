package com.alpacaflow.meditrackplatform;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Validates the JWT of every request before it is routed to a microservice.
 * <p>Public paths (sign-in, sign-up, mock users, documentation and health) are let through.
 * Any other request must carry a valid {@code Authorization: Bearer <token>} header issued by
 * the IAM Service; otherwise the gateway answers 401 and the request never reaches the
 * microservices. The original header is forwarded unchanged, so each microservice can still
 * validate the token and the anti-corruption layers can forward it between services.</p>
 */
@Component
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {

    static final String USER_EMAIL_HEADER = "X-User-Email";
    private static final String BEARER_PREFIX = "Bearer ";

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/authentication/**",
            "/temp-api/v1/users/**",
            "/*/v3/api-docs/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/actuator/health",
            "/error",
            "/favicon.ico");

    private final JwtTokenValidator tokenValidator;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationGlobalFilter(JwtTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        String path = request.getPath().pathWithinApplication().value();

        if (HttpMethod.OPTIONS.equals(request.getMethod()) || isPublic(path)) {
            return chain.filter(exchange);
        }

        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange.getResponse(), "Missing bearer token");
        }

        return tokenValidator.getSubjectIfValid(header.substring(BEARER_PREFIX.length()))
                .map(email -> chain.filter(exchange.mutate()
                        .request(r -> r.headers(h -> h.set(USER_EMAIL_HEADER, email)))
                        .build()))
                .orElseGet(() -> unauthorized(exchange.getResponse(), "Invalid or expired token"));
    }

    /**
     * Runs before the routing filters, so an invalid request is rejected without calling any service.
     */
    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = "{\"message\":\"%s\"}".formatted(message).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(body);
        return response.writeWith(Mono.just(buffer));
    }
}
