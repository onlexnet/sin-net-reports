Feature: User login

  Scenario: Read available projects
    When I login using proper credentials
    Then I may get list of my projects
  
  Scenario: Delete added project
    When I create new project
    Then I may delete just created project