# ============================================================================
# Feature: Basic Application Health Check
# ============================================================================
#
# This feature file describes smoke tests for verifying that the core
# components of the SinNet Reports application are running and accessible.
#
# Written in Gherkin syntax (Cucumber-style BDD)
#
# ============================================================================

Feature: Application Health Check
  As a system administrator
  I want to verify that all core services are running
  So that I can ensure the application is operational

  @smoke @api
  Scenario: WebAPI GraphQL endpoint is accessible
    Given the docker-compose stack is running
    When I check the WebAPI health endpoint
    Then the WebAPI should respond with status 200
    And the health status should be "UP"

  @smoke @api
  Scenario: TimeEntries service is accessible
    Given the docker-compose stack is running
    When I check the TimeEntries health endpoint
    Then the TimeEntries service should respond with status 200
    And the health status should be "UP"

  @smoke @ui
  Scenario: Frontend application loads successfully
    Given the docker-compose stack is running
    When I navigate to the frontend URL
    Then the page should load successfully
    And the page title should contain "SinNet"

  @smoke @api
  Scenario: GraphQL introspection query works
    Given the docker-compose stack is running
    When I send a GraphQL introspection query
    Then the response should contain GraphQL schema information
    And the response should include "Query" type
