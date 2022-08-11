Feature: Timesheet Operations

  Evidence of Timesheets is the core piece of the functionality, as it is PSA-class project
  
  Background:
    Given a project called project1 created by user1
    And a project called project2 created by user2
    And a person named operator1
    
  Rule:

    Scenario: Reject creation of a timesheet by unpermitted person
      When operator1 creates timeentry for project1
      Then operation is rejected
      And number of timesheets in project 1 is zero

    @todo
    Scenario: Create new timesheet by a project operator
      When User user1 creates new project
      Then Number of projects is 1
      And the project is visible on the list of projects

