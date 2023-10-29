Feature: Predefined projects

  Scenario: Avalability of predefined projects
    When biuro@sin.net.pl asks about available projects
    Then list of projects is not null

  Scenario: Avalability of predefined projects for unassigned users
    When undefined@user asks about available projects
    Then list of projects is empty
