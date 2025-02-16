Feature: Manage Timeentries

  Background:
    Given a new project called project1 created by operator0
    And an operator called operator1 assigned to project called project1

  Scenario: Create a new Timeentry
    When operator1 creates new timeentry for project1
    Then operation succeeded
    And the new timeentry is visible on the project1

  Scenario: Ask about report pack
    When operator1 creates new timeentry for project1
    And operator1 requests report1 pack for project1
    Then report1 pack is returned

  Scenario: Update time on singlle secret
    When operator1 creates new timeentry for project1
    And create two secrets on the timeentry
    Then update time on secrets is the same
    And update one of the secrets
    Then update time on secrets is different
    

