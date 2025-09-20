Feature: Report generation

  Scenario: Generate customer report as PDF
    Given project with ID "12345678-1234-1234-1234-123456789012" exists
    And customers exist for the project
    And time entries exist for the customers in the period of year 2023 and month 5
    When report for project "12345678-1234-1234-1234-123456789012" is requested for year 2023 and month 5
    Then PDF report is generated successfully
    And report contains data for all customers
