Feature: User authentication
  As a MediTrack user
  I want to sign up and sign in with my credentials
  So that I can access the platform according to my role

  # US16 - Sign up
  Scenario: Successful sign up
    Given the email "lucia.perez@meditrack.com" is not registered
    When I send a POST request to "/api/v1/authentication/sign-up" with a valid email, password and role
    Then the response status should be 201
    And the response should contain the new user id and email

  Scenario: Sign up with an email already registered
    Given the email "lucia.perez@meditrack.com" is already registered
    When I send a POST request to "/api/v1/authentication/sign-up" with that email
    Then the response status should be 400

  # US15 - Sign in
  Scenario: Successful sign in
    Given a registered user with email "lucia.perez@meditrack.com" and password "Secret123"
    When I send a POST request to "/api/v1/authentication/sign-in" with those credentials
    Then the response status should be 200
    And the response should contain a JWT token

  Scenario: Sign in with wrong credentials
    Given a registered user with email "lucia.perez@meditrack.com"
    When I send a POST request to "/api/v1/authentication/sign-in" with a wrong password
    Then the sign in should be rejected
    And no JWT token should be returned
