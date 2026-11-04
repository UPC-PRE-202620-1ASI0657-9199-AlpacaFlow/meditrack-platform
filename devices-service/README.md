# devices-service

MediTrack microservice for the **Devices** bounded context, extracted from the `meditrack-platform` monolith.

## Responsibilities
- Smart patches (devices) registration and lookup.
- Since Sprint 2, vital sign measurements and alerts belong to `monitoring-service` (port 8085),
  which asks this service whether a device exists before storing its first measurement.

## Changes from the monolith
- Own Spring Boot application (`DevicesServiceApplication`) on port **8083**.
- Own database: `meditrack_devices` (database per service).
- This context has no in-memory dependencies on other bounded contexts, so no anti-corruption layer is needed.
- Does not store users: it only validates JWTs issued by `iam-service` (shared secret).

## Run
```bash
./mvnw spring-boot:run
```
Swagger UI: http://localhost:8083/swagger-ui/index.html

Use the token from `iam-service` (`POST /api/v1/authentication/sign-in`) in **Authorize**.
