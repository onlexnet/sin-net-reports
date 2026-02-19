# E2E Smoke Tests

Browser-based smoke tests for SinNet Reports using Playwright and pytest-bdd (BDD/Cucumber-style testing in Python).

## 🚀 Quick Start

### Prerequisites
- Python 3.12+
- k3d local stack (for running the application services)
- pip (Python package manager)

### Setup

1. **Install Python dependencies:**
   ```bash
   cd smoke-test/e2e
   pip install -r requirements.txt
   ```

2. **Install Playwright browsers:**
   ```bash
   playwright install
   ```
   This downloads Chromium, Firefox, and WebKit browser binaries.

3. **Start the application stack:**
   ```bash
   cd ../  # Back to smoke-test directory
    ./setup-k3d.sh up
   ```

4. **Run tests (in another terminal):**
   ```bash
   cd smoke-test/e2e
   pytest
   ```

## 📖 Usage

### Run All Tests
```bash
pytest
```

### Run with Visible Browser (for debugging)
```bash
pytest --headed
```

### Run Specific Tests by Marker
```bash
pytest -m smoke      # Only smoke tests
pytest -m ui         # Only UI tests
pytest -m api        # Only API tests
```

### Run with Verbose Output
```bash
pytest -v -s
```

### Generate HTML Report
```bash
pytest --html=reports/test-report.html --self-contained-html
```

### Run in Different Browser
```bash
pytest --browser firefox
pytest --browser webkit  # Safari engine
```

## 📁 Project Structure

```
e2e/
├── features/              # Gherkin feature files (BDD scenarios)
│   └── health_check.feature
├── step_defs/            # Step definitions (Python implementations)
│   └── test_health_check_steps.py
├── tests/                # Additional test files (if needed)
├── reports/              # Generated test reports (created automatically)
├── conftest.py           # Pytest configuration and fixtures
├── pytest.ini            # Pytest settings
├── requirements.txt      # Python dependencies
└── README.md            # This file
```

## ✍️ Writing New Tests

### 1. Create a Feature File (Gherkin)

Create `features/my_feature.feature`:
```gherkin
Feature: User Login
  As a user
  I want to log in to the application
  So that I can access my data

  @smoke @ui
  Scenario: Successful login
    Given I am on the login page
    When I enter valid credentials
    And I click the login button
    Then I should see the dashboard
```

### 2. Implement Step Definitions

Create `step_defs/test_my_feature_steps.py`:
```python
from pytest_bdd import given, when, then, scenarios

# Load scenarios from feature file
scenarios('../features/my_feature.feature')

@given("I am on the login page")
def navigate_to_login(page):
    page.goto("http://localhost:3000/login")

@when("I enter valid credentials")
def enter_credentials(page):
    page.fill("#username", "testuser")
    page.fill("#password", "testpass")

@when("I click the login button")
def click_login(page):
    page.click("button[type='submit']")

@then("I should see the dashboard")
def verify_dashboard(page):
    assert page.is_visible("text=Dashboard")
```

## 🎯 Test Markers

Tests are organized using pytest markers:

- `@smoke`: Critical functionality tests (fast, run in CI)
- `@ui`: Browser-based UI tests
- `@api`: API endpoint tests (no browser needed)
- `@slow`: Tests taking more than 1 second

## 🔧 Configuration

### pytest.ini
Main pytest configuration file. Customize:
- Test discovery patterns
- Default command-line options
- Browser settings
- Base URL

### conftest.py
Pytest fixtures and hooks:
- `context`: Shared data between test steps
- `browser_context_args`: Browser configuration
- `page`: Page-level settings

### Environment Variables
- `WEBAPI_URL`: GraphQL API endpoint (default: http://localhost:11031)
- `TIMEENTRIES_URL`: TimeEntries service (default: http://localhost:11021)
- `FRONTEND_URL`: React frontend (default: http://localhost:3000)

## 🐛 Debugging

### Run with Browser Visible
```bash
pytest --headed --slowmo=1000
```
- `--headed`: Show browser window
- `--slowmo`: Slow down operations (milliseconds)

### Screenshot on Failure
Playwright automatically takes screenshots on failure:
- Location: `test-results/` directory
- Includes browser console logs and network traffic

### Print Debug Information
```bash
pytest -v -s
```
- `-v`: Verbose test names
- `-s`: Show print statements (don't capture stdout)

### Pause Test Execution
Add `page.pause()` in your test to open Playwright Inspector:
```python
def my_test_step(page):
    page.goto("http://localhost:3000")
    page.pause()  # Opens interactive debugger
    # Test continues after closing inspector
```

## 🚢 CI/CD Integration

Tests can run in GitHub Actions or any CI system:

```yaml
- name: Run E2E Tests
  run: |
    cd smoke-test/e2e
    pip install -r requirements.txt
    playwright install --with-deps chromium
    pytest --browser chromium --html=reports/test-report.html
```

## 📚 Resources

- [pytest-bdd documentation](https://pytest-bdd.readthedocs.io/)
- [Playwright Python documentation](https://playwright.dev/python/)
- [pytest documentation](https://docs.pytest.org/)
- [Gherkin syntax reference](https://cucumber.io/docs/gherkin/reference/)

## ❓ Troubleshooting

### "Connection refused" errors
Make sure the k3d stack is running:
```bash
cd smoke-test
./setup-k3d.sh up
```

### Browser not found
Install Playwright browsers:
```bash
playwright install
```

### Tests hang or timeout
Increase timeouts in `conftest.py` or use `--timeout` option:
```bash
pytest --timeout=60
```
