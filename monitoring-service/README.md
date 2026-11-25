# monitoring-service

MediTrack microservice for the **Monitoring** bounded context, extracted from `devices-service` in Sprint 2.

## Responsibilities
- Vital sign measurements per smart patch: heart rate, oxygen saturation, temperature and blood pressure.
- Alerts generated when a measurement is outside the safe range (`AlertCreatedEvent` → `Alert`).

## Design
- Own Spring Boot application (`MonitoringServiceApplication`) on port **8085**.
- Own database: `meditrack_monitoring` (database per service).
- Aggregate `MonitoredDevice`: keeps only the `deviceId` of the patch (no foreign key to the Devices database) and its measurements (latest 7 per type).
- Anti-corruption layer `ExternalDeviceService`: on the first measurement of a device, asks `devices-service` (`GET /api/v1/devices/{id}`) whether the device exists, forwarding the caller's JWT.
  - Device not found → **404**. Devices Service not reachable → **503**.
- Anti-corruption layer `ExternalNotificationService` (Sprint 3): after saving an alert, sends it to `notification-service` (`POST /api/v1/notifications`, port 8086), forwarding the caller's JWT. Best-effort: if the Notification Service is down, the alert is still saved and the failure is logged.
- The REST paths are the same ones `devices-service` exposed before, so the frontend does not change; the API Gateway routes them here:
  - `POST|GET /api/v1/devices/{deviceId}/measurements/{heart-rate|oxygen|temperature|blood-pressure}`
  - `GET /api/v1/alerts`, `GET /api/v1/alerts/{alertId}`
- Does not store users: it only validates JWTs issued by `iam-service` (shared secret).

## Run
Start `devices-service` (8083) and `notification-service` (8086) first, then:
```bash
./mvnw spring-boot:run
```
Swagger UI: http://localhost:8085/swagger-ui/index.html
