Feature: Manage Customers

  Background:
    Given a new project called project1 created by operator0
    And an operator called operator1 assigned to project called project1
    And an operator called operator2 assigned to project called project1

  Scenario: Create a new Project
    When operator1 creates a new customer named customer1
    Then operator1 is able to change the name of the Customer
    And operator1 is able to change all properties of the Customer

  Scenario: Update time on a single secret
    When operator1 creates a new customer named customer1
    And create two secrets on the customer
    Then update time on secrets is the same
    And update one of the secrets
    Then update time on secrets is different

