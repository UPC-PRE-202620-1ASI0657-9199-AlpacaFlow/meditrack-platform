Feature: Health alert notifications
  As a relative or doctor
  I want to be notified when the monitoring raises a health alert
  So that I can act quickly on important changes in the senior citizen's vital signs

  Background:
    Given I am authenticated with a valid JWT token
    And the device 1 exists in the devices service

  # US39 - Recibir alertas de salud
  Scenario: Out-of-range measurement creates a notification
    When I send a POST request to "/api/v1/devices/1/measurements/heart-rate" with 145 bpm
    Then the response status should be 201
    And a new alert should appear in "/api/v1/alerts"
    And a notification with status "UNREAD" should appear in "/api/v1/notifications" for that alert

  Scenario: Measurement inside the safe range does not notify
    When I send a POST request to "/api/v1/devices/1/measurements/heart-rate" with 78 bpm
    Then the response status should be 201
    And no new notification should appear in "/api/v1/notifications"

  # US24 / US28 - Recibir alertas críticas
  Scenario: Notifications of one smart patch are listed newest first
    Given the device 1 has 2 notifications
    When I send a GET request to "/api/v1/notifications?deviceId=1"
    Then the response status should be 200
    And the response should contain 2 notifications ordered from newest to oldest

  Scenario: Notification marked as read
    Given a notification with id 1 and status "UNREAD"
    When I send a PATCH request to "/api/v1/notifications/1/read"
    Then the response status should be 200
    And the notification status should be "READ"
    And the read date should be registered

  Scenario: Notification that does not exist
    When I send a GET request to "/api/v1/notifications/999"
    Then the response status should be 404

  Scenario: The same alert is not notified twice
    Given the alert 1 already has a notification
    When the monitoring service sends the alert 1 again to "/api/v1/notifications"
    Then the response status should be 201
    And there should still be only one notification for the alert 1

  Scenario: The alert is saved even if the notification service is down
    Given the notification service is not available
    When I send a POST request to "/api/v1/devices/1/measurements/heart-rate" with 145 bpm
    Then the response status should be 201
    And a new alert should appear in "/api/v1/alerts"

  Scenario: Request without token
    Given I am not authenticated
    When I send a GET request to "/api/v1/notifications"
    Then the response status should be 401
