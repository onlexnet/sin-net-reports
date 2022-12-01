Feature: Manage Timeentries

  Background:
    Given a new project called "project1"
    And an operator called "operator1" assigned to project called "project1"

  Scenario: Create a new Timeentry
    When operator called "operator1" creates new timeentry
    Then operation succeeded
