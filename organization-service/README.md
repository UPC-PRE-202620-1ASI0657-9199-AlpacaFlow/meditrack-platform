# organization-service

MediTrack microservice for the **Organization** bounded context, extracted from the `meditrack-platform` monolith.

## Responsibilities
- Organizations (clinics and relative accounts) and their administrators.
- Doctors, caregivers and senior citizens, and the doctor/caregiver assignments.

## Changes from the monolith
- Own Spring Boot application (`OrganizationServiceApplication`) on port **8082**.
- Own database: `meditrack_organization` (database per service).
- The in-memory calls to the IAM `UserCommandService` (in `AdminCommandServiceImpl`, `DoctorCommandServiceImpl`
  and `CaregiverCommandServiceImpl`) were replaced by `ExternalIamService`, an anti-corruption layer that calls the
  IAM Service over HTTP (`GET /api/v1/users/{id}`, `GET /api/v1/users/email/{email}`, `POST /api/v1/users`),
  forwarding the caller's JWT.
- Does not store users: it only validates JWTs issued by `iam-service` (shared secret).

## Run
```bash
./mvnw spring-boot:run
```
Swagger UI: http://localhost:8082/swagger-ui/index.html

Requires `iam-service` running on port 8081.
