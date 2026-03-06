# ============================================================================
# Feature: User Login (static-webapp-time)
# ============================================================================
#
# This feature validates that a seeded user can login and land on the
# authenticated welcome screen in the new shadcn-based webapp.
#
# ============================================================================

Feature: User Login (Time Webapp)
  As an authenticated user
  I want to login successfully in the new shadcn-based webapp
  So that I can access the main application modules

  @smoke @ui @webapp-time
  Scenario: User can login and see welcome screen in time webapp
    Given the k3d stack is running
    When I login to the time webapp as "user1@project1"
    Then the time webapp page should contain "Witaj w systemie ewidencji usług"
    And the time webapp menu should contain "Usługi" and "Klienci"
