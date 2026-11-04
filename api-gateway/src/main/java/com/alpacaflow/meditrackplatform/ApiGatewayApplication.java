package com.alpacaflow.meditrackplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MediTrack API Gateway.
 * <p>Single entry point of the platform: the frontend only talks to this application,
 * which validates the JWT issued by the IAM Service and routes each request to the
 * microservice that owns the bounded context (see {@code application.yml}).</p>
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
