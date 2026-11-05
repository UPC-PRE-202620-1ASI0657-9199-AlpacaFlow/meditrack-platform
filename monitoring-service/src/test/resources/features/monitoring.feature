Feature: Vital signs monitoring
  As a relative or doctor
  I want the readings of the smart patch to be stored and checked
  So that I am alerted when a vital sign is out of the safe range

  Background:
    Given I am authenticated with a valid JWT token
    And the device 1 exists in the devices service

  # TS23 - Register heart rate
  Scenario: Heart rate registered successfully
    When I send a POST request to "/api/v1/devices/1/measurements/heart-rate" with 78 bpm
    Then the response status should be 201
    And no alert should be created

  Scenario: Measurement for a device that does not exist
    Given the device 999 does not exist in the devices service
    When I send a POST request to "/api/v1/devices/999/measurements/heart-rate" with 78 bpm
    Then the response status should be 404

  # US39 - Health alert
  Scenario: Heart rate out of the safe range creates an alert
    When I send a POST request to "/api/v1/devices/1/measurements/heart-rate" with 145 bpm
    Then the response status should be 201
    And a new alert should appear in "/api/v1/alerts"

  # TS24 - Query heart rate
  Scenario: Heart rate history listed successfully
    Given the device 1 has heart rate measurements
    When I send a GET request to "/api/v1/devices/1/measurements/heart-rate"
    Then the response status should be 200
    And the response should contain a list of measurements
