Feature: Relatives management
  As a relative of a senior citizen
  I want to register myself and see my information
  So that I can follow the health of my family member

  Background:
    Given I am authenticated with a valid JWT token

  Scenario: Relative registered successfully
    Given the senior citizen 1 belongs to a relative organization in the organization service
    When I send a POST request to "/api/v1/relatives" with my user id and the senior citizen id 1
    Then the response status should be 201
    And the response should include the senior citizen data

  Scenario: Relative linked to a senior citizen that does not exist
    Given the senior citizen 999 does not exist in the organization service
    When I send a POST request to "/api/v1/relatives" with the senior citizen id 999
    Then the request should be rejected
    And no relative should be created

  # TS16 - Query the relative of a user
  Scenario: Relative of a user found
    Given the user 3 has a registered relative
    When I send a GET request to "/api/v1/relatives/user/3"
    Then the response status should be 200
    And the response should contain the relative of that user
