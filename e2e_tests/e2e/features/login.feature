# ============================================================================
# Feature: User Login
# ============================================================================
#
# This feature validates that a seeded user can login and land on the
# authenticated welcome screen with expected navigation items.
#
# ============================================================================

Feature: User Login
  As an authenticated user
  I want to login successfully
  So that I can access the main application modules

  @smoke @ui
  Scenario: User can login and see welcome screen menu
    Given the k3d stack is running
    When I login as "user1@project1"
    Then the page should contain "Witaj w systemie ewidencji usług"
    And the menu should contain "Usługi" and "Klienci"
