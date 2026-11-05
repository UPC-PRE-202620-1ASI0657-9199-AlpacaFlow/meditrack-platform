Feature: Device management
  As an organization administrator
  I want to register and query smart patches
  So that each senior citizen can be monitored

  Background:
    Given I am authenticated with a valid JWT token

  # TS17 - Register device
  Scenario: Device registered successfully
    When I send a POST request to "/api/v1/devices" with a model and a holder id
    Then the response status should be 201
    And the response should contain the new device id

  Scenario: Device with missing fields
    When I send a POST request to "/api/v1/devices" without a holder id
    Then the response status should be 400

  # TS18 - List devices
  Scenario: Devices listed successfully
    Given there are registered devices
    When I send a GET request to "/api/v1/devices"
    Then the response status should be 200
    And the response should contain a list of devices

  # TS19 - Get device by id
  Scenario: Device found
    Given the device 1 exists
    When I send a GET request to "/api/v1/devices/1"
    Then the response status should be 200

  Scenario: Device not found
    Given the device 999 does not exist
    When I send a GET request to "/api/v1/devices/999"
    Then the response status should be 404
