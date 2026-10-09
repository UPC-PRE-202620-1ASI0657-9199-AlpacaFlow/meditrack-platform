# iam-service

Microservicio de **Identity and Access Management** de MediTrack, extraído del monolito `meditrack-platform`.

## Responsabilidades
- Registro de usuarios (`POST /api/v1/authentication/sign-up`)
- Inicio de sesión y emisión de JWT (`POST /api/v1/authentication/sign-in`)
- Usuarios de prueba en perfil `dev` (`/temp-api/v1/users`)

## Cambios respecto al monolito
- Proyecto Spring Boot independiente (`IamServiceApplication`), puerto **8081**.
- Base de datos propia: `meditrack_iam` (se crea sola si no existe).
- La creación de organización y administrador al registrarse un admin ya no llama en memoria al contexto Organization:
  ahora usa `ExternalOrganizationService` (anti-corruption layer) que llama por HTTP al Organization Service
  (`services.organization.url`, por defecto `http://localhost:8082`).

## Ejecutar
```bash
./mvnw spring-boot:run
```
Swagger UI: http://localhost:8081/swagger-ui/index.html
