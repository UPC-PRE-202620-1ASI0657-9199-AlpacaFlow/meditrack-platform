# relatives-service

MediTrack microservice for the **Relatives** bounded context, extracted from the `meditrack-platform` monolith.

## Responsibilities
- Register a relative (family member) and link the senior citizen they monitor.
- Query relatives by id or by IAM user id.

## Changes from the monolith
- Own Spring Boot application (`RelativesServiceApplication`) on port **8084**.
- Own database: `meditrack_relatives` (database per service).
- The in-memory call to `SeniorCitizenQueryService` (Organization context) was replaced by
  `ExternalOrganizationService`, an anti-corruption layer that calls the Organization Service over HTTP
  (`GET /api/v1/senior-citizens/{id}` and `GET /api/v1/organizations/{id}`), forwarding the caller's JWT.
- Does not store users: it only validates JWTs issued by `iam-service` (shared secret).

## Run
```bash
./mvnw spring-boot:run
```
Swagger UI: http://localhost:8084/swagger-ui/index.html

Use the token from `iam-service` (`POST /api/v1/authentication/sign-in`) in **Authorize**.
`POST /api/v1/relatives` requires `organization-service` running on port 8082.
