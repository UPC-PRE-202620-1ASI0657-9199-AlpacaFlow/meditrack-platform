# api-gateway

Single entry point of MediTrack. The frontend only calls the gateway, which validates the JWT
issued by `iam-service` and routes each request to the microservice that owns the bounded context.

## Responsibilities
- **Routing** by path to the microservices (`application.yml`).
- **JWT validation** before routing (`JwtAuthenticationGlobalFilter`): requests without a valid
  token get `401` and never reach the services. Sign-in, sign-up, mock users, documentation and
  health are public.
- **Centralized CORS** for the frontend.
- **Unified Swagger UI** that shows the OpenAPI document of every microservice.

## Routes

| Path | Microservice | Port |
|---|---|---|
| `/api/v1/authentication/**`, `/api/v1/users/**`, `/temp-api/v1/users/**` | iam-service | 8081 |
| `/api/v1/organizations/**`, `/api/v1/admins/**`, `/api/v1/caregivers/**`, `/api/v1/doctors/**`, `/api/v1/senior-citizens/**`, `/api/v1/caregiver-assignments/**`, `/api/v1/doctor-assignments/**` | organization-service | 8082 |
| `/api/v1/devices/**`, `/api/v1/alerts/**` | devices-service | 8083 |
| `/api/v1/relatives/**` | relatives-service | 8084 |

The gateway uses port **8080**, the port the frontend already used with the monolith.
Do not run the monolith (`src/` at the repository root) at the same time.

## Run
1. Start the microservices (8081–8084).
2. Start the gateway:
```bash
./mvnw spring-boot:run
```
- Swagger UI (all services): http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

Sign in through the gateway (`POST http://localhost:8080/api/v1/authentication/sign-in`) and use the
token in **Authorize**; every other call also goes through `http://localhost:8080`.

## Tests
```bash
./mvnw test
```
`JwtAuthenticationGlobalFilterTests` checks that protected routes reject missing or invalid tokens
and that public routes are let through.
