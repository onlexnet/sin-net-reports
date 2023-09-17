Feature: An example

  Scenario: Predefined projects
    When user is requesting list of projects
    Then Project uservice is requested
    And Response is returned

  Scenario: Save project
    When a project is saved
    Then operation result is returned

  Scenario: User stats
    When userstats request is send to backend
    Then userstats are returned

  Scenario: Customer creation
    When Customer creation request is send to backend
    Then Customer creation result is verified

  Scenario: Customer save
    When Customer save request is send to backend
    Then Customer save result is verified

  Scenario: Customer read
    When Customer read request is send to backend
    Then Customer read result is verified

  Scenario: Customer list
    When Customer list request is send to backend
    Then Customer list result is verified

  Scenario: Users list query
    When Users list query is send
    Then Users list response is returned

  Scenario: Users list actions
    When Actions list query is send
    Then Actions list response is returned

  Scenario: User creates new Action
    When Actions create command is send
    Then Actions create result is returned

  Scenario: User gets existing Action
    When Actions get query is sent
    Then Actions get query is returned

