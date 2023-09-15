Feature: Manage Customers

  Background:
    Given a new project called project1
    And an operator called operator1 assigned to project called project1
    And an operator called operator2 assigned to project called project1

  Scenario: Create a new Project
    When operator1 creates a new customer
    Then operator1 is able to change the name of the Customer
