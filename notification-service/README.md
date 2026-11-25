# notification-service

MediTrack microservice for the **Notification** bounded context, added in Sprint 3.

## Responsibilities
- Turns each alert raised by `monitoring-service` into a notification for the user.
- Lets the frontend list the notifications (all or by smart patch) and mark them as read.

## Design
- Own Spring Boot application (`NotificationServiceApplication`) on port **8086**.
- Own database: `meditrack_notifications` (database per service).
- Aggregate `Notification`: keeps only the `alertId` and the `deviceId` (no foreign keys to other databases), the severity (`DANGER` / `WARNING`), the message and its status (`UNREAD` → `READ`).
- Idempotent creation: one notification per `alertId`; if the Monitoring Service sends the same alert twice, the existing notification is returned.
- `monitoring-service` calls it through the anti-corruption layer `ExternalNotificationService` right after saving an alert, forwarding the caller's JWT. The call is best-effort: if this service is down, the alert is still saved and the failure is logged.
- Does not store users: it only validates JWTs issued by `iam-service` (shared secret).

## Endpoints (through the API Gateway, http://localhost:8080)
| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/notifications` | Create a notification (used by `monitoring-service`) |
| GET | `/api/v1/notifications` | All notifications, newest first |
| GET | `/api/v1/notifications?deviceId={id}` | Notifications of one smart patch |
| GET | `/api/v1/notifications/{id}` | One notification |
| PATCH | `/api/v1/notifications/{id}/read` | Mark as read |

## Run
Start MySQL, then:
```bash
./mvnw spring-boot:run
```
Swagger UI: http://localhost:8086/swagger-ui/index.html (also in the unified Swagger UI of the gateway).

End-to-end check: register an out-of-range measurement (for example a heart rate of 145 bpm) through the gateway; the alert appears in `GET /api/v1/alerts` and its notification in `GET /api/v1/notifications`.
