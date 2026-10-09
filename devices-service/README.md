# devices-service

MediTrack microservice for the **Devices** bounded context, extracted from the `meditrack-platform` monolith.

## Responsibilities
- Smart patches (devices) registration and lookup.
- Vital sign measurements per device: heart rate, oxygen, temperature and blood pressure.
- Alerts generated when a measurement is out of range.

## Changes from the monolith
- Own Spring Boot application (`DevicesServiceApplication`) on port **8083**.
- Own database: `meditrack_devices` (database per service).
- This context had no in-memory dependencies on other bounded contexts, so no anti-corruption layer was needed.
  The internal domain event `AlertCreatedEvent` stays inside the service.
- Does not store users: it only validates JWTs issued by `iam-service` (shared secret).

## Run
```bash
./mvnw spring-boot:run
```
Swagger UI: http://localhost:8083/swagger-ui/index.html

Use the token from `iam-service` (`POST /api/v1/authentication/sign-in`) in **Authorize**.
