Feature: Organization management
  As an organization administrator
  I want to register my organization and manage its members
  So that the platform can monitor the senior citizens in my care

  Background:
    Given I am authenticated with a valid JWT token

  # TS1 - Register organization
  Scenario: Organization registered successfully
    When I send a POST request to "/api/v1/organizations" with a name, address and email
    Then the response status should be 201
    And the response should contain the new organization id

  Scenario: Organization with missing fields
    When I send a POST request to "/api/v1/organizations" without a name
    Then the response status should be 400

  # TS3 - List admins of an organization
  Scenario: Admins listed successfully
    Given the organization 1 has registered admins
    When I send a GET request to "/api/v1/admins/organization/1"
    Then the response status should be 200
    And the response should contain a list of admins

  # TS11 - List senior citizens of an organization
  Scenario: Senior citizens listed successfully
    Given the organization 1 has registered senior citizens
    When I send a GET request to "/api/v1/senior-citizens/organization/1"
    Then the response status should be 200
    And the response should contain a list of senior citizens

  Scenario: Organization without senior citizens
    Given the organization 2 has no senior citizens
    When I send a GET request to "/api/v1/senior-citizens/organization/2"
    Then the response status should be 200
    And the response should be an empty list
