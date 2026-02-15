"""
============================================================================
Step Definitions for Health Check Feature
============================================================================

This module implements the step definitions for the health_check.feature file.
It uses pytest-bdd to map Gherkin steps to Python functions.

Step types:
- @given: Setup/preconditions
- @when: Actions/operations
- @then: Assertions/verifications

============================================================================
"""

import requests
from pytest_bdd import given, when, then, parsers, scenarios

# Load all scenarios from the feature file
scenarios('../features/health_check.feature')

# ============================================================================
# Configuration
# ============================================================================

WEBAPI_URL = "http://localhost:11031"
TIMEENTRIES_URL = "http://localhost:11021"
FRONTEND_URL = "http://localhost:3000"

# ============================================================================
# Given Steps (Preconditions)
# ============================================================================

@given("the docker-compose stack is running")
def docker_compose_running():
    """
    Verify that docker-compose stack is running.
    This is a precondition check that can be extended to verify
    docker containers are actually running.
    """
    # This step is mainly declarative - the actual check happens
    # when we try to connect to services in subsequent steps
    pass

# ============================================================================
# When Steps (Actions)
# ============================================================================

@when("I check the WebAPI health endpoint")
def check_webapi_health(context):
    """
    Make HTTP request to WebAPI health endpoint.
    Spring Boot Actuator provides /actuator/health endpoint.
    """
    try:
        context['response'] = requests.get(
            f"{WEBAPI_URL}/actuator/health",
            timeout=5
        )
    except requests.exceptions.RequestException as e:
        context['error'] = str(e)
        context['response'] = None

@when("I check the TimeEntries health endpoint")
def check_timeentries_health(context):
    """
    Make HTTP request to TimeEntries health endpoint.
    Spring Boot Actuator provides /actuator/health endpoint.
    """
    try:
        context['response'] = requests.get(
            f"{TIMEENTRIES_URL}/actuator/health",
            timeout=5
        )
    except requests.exceptions.RequestException as e:
        context['error'] = str(e)
        context['response'] = None

@when("I navigate to the frontend URL")
def navigate_to_frontend(page, context):
    """
    Use Playwright to navigate to the frontend application.
    The 'page' fixture is provided by pytest-playwright.
    """
    try:
        context['page_response'] = page.goto(FRONTEND_URL, timeout=10000)
        context['page'] = page
    except Exception as e:
        context['error'] = str(e)
        context['page_response'] = None

@when("I send a GraphQL introspection query")
def send_graphql_introspection(context):
    """
    Send GraphQL introspection query to discover schema.
    This query is used by GraphQL tools to explore the API.
    """
    introspection_query = """
    query IntrospectionQuery {
        __schema {
            queryType { name }
            types {
                name
                kind
            }
        }
    }
    """
    
    try:
        context['response'] = requests.post(
            f"{WEBAPI_URL}/graphql",
            json={'query': introspection_query},
            headers={'Content-Type': 'application/json'},
            timeout=5
        )
    except requests.exceptions.RequestException as e:
        context['error'] = str(e)
        context['response'] = None

# ============================================================================
# Then Steps (Assertions)
# ============================================================================

@then(parsers.parse("the WebAPI should respond with status {status_code:d}"))
def verify_webapi_status(context, status_code):
    """
    Verify that WebAPI responded with expected HTTP status code.
    """
    assert context.get('response') is not None, \
        f"No response received. Error: {context.get('error', 'Unknown')}"
    assert context['response'].status_code == status_code, \
        f"Expected status {status_code}, got {context['response'].status_code}"

@then(parsers.parse("the TimeEntries service should respond with status {status_code:d}"))
def verify_timeentries_status(context, status_code):
    """
    Verify that TimeEntries service responded with expected HTTP status code.
    """
    assert context.get('response') is not None, \
        f"No response received. Error: {context.get('error', 'Unknown')}"
    assert context['response'].status_code == status_code, \
        f"Expected status {status_code}, got {context['response'].status_code}"

@then(parsers.parse('the health status should be "{expected_status}"'))
def verify_health_status(context, expected_status):
    """
    Verify that health endpoint reports expected status.
    Spring Boot Actuator health response: {"status": "UP"}
    """
    assert context.get('response') is not None, "No response received"
    health_data = context['response'].json()
    actual_status = health_data.get('status', 'UNKNOWN')
    assert actual_status == expected_status, \
        f"Expected health status '{expected_status}', got '{actual_status}'"

@then("the page should load successfully")
def verify_page_loaded(context):
    """
    Verify that frontend page loaded without errors.
    """
    assert context.get('page_response') is not None, \
        f"Page failed to load. Error: {context.get('error', 'Unknown')}"
    assert context['page_response'].ok, \
        f"Page loaded with HTTP error: {context['page_response'].status}"

@then(parsers.parse('the page title should contain "{expected_text}"'))
def verify_page_title(context, expected_text):
    """
    Verify that page title contains expected text.
    """
    page = context.get('page')
    assert page is not None, "No page object available"
    
    title = page.title()
    assert expected_text.lower() in title.lower(), \
        f"Expected page title to contain '{expected_text}', got '{title}'"

@then("the response should contain GraphQL schema information")
def verify_graphql_schema_present(context):
    """
    Verify that GraphQL introspection query returned schema data.
    """
    assert context.get('response') is not None, "No response received"
    assert context['response'].status_code == 200, \
        f"GraphQL query failed with status {context['response'].status_code}"
    
    data = context['response'].json()
    assert 'data' in data, "Response does not contain 'data' field"
    assert '__schema' in data['data'], "Schema information not found in response"

@then(parsers.parse('the response should include "{type_name}" type'))
def verify_graphql_type_exists(context, type_name):
    """
    Verify that GraphQL schema includes expected type.
    """
    data = context['response'].json()
    query_type = data['data']['__schema']['queryType']['name']
    assert query_type == type_name, \
        f"Expected query type '{type_name}', got '{query_type}'"
