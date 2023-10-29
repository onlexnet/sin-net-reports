Feature: Predefined projects

  Scenario: Avalability of predefined porjects
    When biuro@sin.net.pl asks about available projects
    Then list of projects is not null
