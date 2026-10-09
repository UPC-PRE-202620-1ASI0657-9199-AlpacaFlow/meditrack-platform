package com.alpacaflow.meditrackplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * IAM Service entry point.
 * <p>Bounded context extracted from the MediTrack monolith: user registration,
 * authentication and JWT issuance.</p>
 */
@EnableJpaAuditing
@SpringBootApplication
public class IamServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IamServiceApplication.class, args);
    }
}
