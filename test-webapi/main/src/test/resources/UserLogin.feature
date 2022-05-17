Feature: User login

  Background: Logged user
    When I am logged in as Operator1
    
  Scenario: Delete added project
    When I create new project
    Then I may delete just created project

  Scenario: Read available projects
    When I login using proper credentials
    Then I may get list of my projects
  