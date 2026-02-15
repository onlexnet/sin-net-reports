#!/bin/bash

# ============================================================================
# E2E Smoke Test Runner
# ============================================================================
#
# This script simplifies running E2E smoke tests locally.
# It checks prerequisites and provides helpful guidance.
#
# USAGE:
#   ./run-tests.sh [options]
#
# OPTIONS:
#   --headed      Run with visible browser (for debugging)
#   --slow        Slow down operations for observation
#   --setup-only  Only install dependencies, don't run tests
#   --help        Show this help message
#
# ============================================================================

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Default options
HEADED=""
SLOWMO=""
SETUP_ONLY=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    --headed)
      HEADED="--headed"
      shift
      ;;
    --slow)
      SLOWMO="--slowmo=1000"
      shift
      ;;
    --setup-only)
      SETUP_ONLY=true
      shift
      ;;
    --help)
      grep '^#' "$0" | sed 's/^# \?//'
      exit 0
      ;;
    *)
      echo -e "${RED}Unknown option: $1${NC}"
      echo "Use --help for usage information"
      exit 1
      ;;
  esac
done

echo -e "${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║           E2E Smoke Test Runner for SinNet Reports            ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Check if we're in the correct directory
if [ ! -f "requirements.txt" ]; then
  echo -e "${RED}❌ Error: requirements.txt not found${NC}"
  echo -e "${YELLOW}Please run this script from the smoke-test/e2e directory:${NC}"
  echo "  cd smoke-test/e2e"
  echo "  ./run-tests.sh"
  exit 1
fi

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
  echo -e "${RED}❌ Error: Python 3 is not installed${NC}"
  echo "Please install Python 3.12 or later"
  exit 1
fi

PYTHON_VERSION=$(python3 --version | cut -d' ' -f2)
echo -e "${GREEN}✓${NC} Python version: $PYTHON_VERSION"

# Check if pip is installed
if ! command -v pip3 &> /dev/null; then
  echo -e "${RED}❌ Error: pip is not installed${NC}"
  echo "Please install pip for Python package management"
  exit 1
fi

# Check if docker-compose stack is running
echo ""
echo -e "${BLUE}Checking if services are running...${NC}"

if curl -sf http://localhost:11031/actuator/health > /dev/null 2>&1; then
  echo -e "${GREEN}✓${NC} WebAPI is running on port 11031"
else
  echo -e "${YELLOW}⚠️  WebAPI is not responding on port 11031${NC}"
  echo -e "${YELLOW}Please start the docker-compose stack first:${NC}"
  echo "  cd ../  # Go to smoke-test directory"
  echo "  docker compose --env-file .env.CI up"
  echo ""
  echo -e "${YELLOW}Then run this script again in another terminal.${NC}"
  exit 1
fi

if curl -sf http://localhost:11021/actuator/health > /dev/null 2>&1; then
  echo -e "${GREEN}✓${NC} TimeEntries service is running on port 11021"
else
  echo -e "${YELLOW}⚠️  TimeEntries service is not responding${NC}"
fi

if curl -sf http://localhost:3000 > /dev/null 2>&1; then
  echo -e "${GREEN}✓${NC} Frontend is running on port 3000"
else
  echo -e "${YELLOW}⚠️  Frontend is not responding${NC}"
fi

# Install Python dependencies
echo ""
echo -e "${BLUE}Installing Python dependencies...${NC}"
pip3 install -q -r requirements.txt
echo -e "${GREEN}✓${NC} Python dependencies installed"

# Install Playwright browsers
echo ""
echo -e "${BLUE}Installing Playwright browsers (if not already installed)...${NC}"
if python3 -m playwright install chromium --with-deps 2>&1 | grep -q "Playwright build"; then
  echo -e "${GREEN}✓${NC} Playwright browsers installed"
else
  # Already installed or minor warning - check if it's usable
  if python3 -m playwright --version > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Playwright browsers ready"
  else
    echo -e "${YELLOW}⚠️  Playwright installation may have issues${NC}"
    echo -e "${YELLOW}Try running manually: playwright install chromium --with-deps${NC}"
  fi
fi

if [ "$SETUP_ONLY" = true ]; then
  echo ""
  echo -e "${GREEN}✅ Setup completed successfully!${NC}"
  echo ""
  echo "To run tests manually:"
  echo "  pytest"
  exit 0
fi

# Run tests
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                    Running E2E Tests                           ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Build pytest command with options
PYTEST_CMD="pytest -v --browser chromium $HEADED $SLOWMO"

echo "Command: $PYTEST_CMD"
echo ""

# Run tests and capture exit code
set +e  # Don't exit on test failure
$PYTEST_CMD
TEST_EXIT_CODE=$?
set -e

# Display results
echo ""
echo -e "${BLUE}╔════════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                      Test Results                              ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════════╝${NC}"
echo ""

if [ $TEST_EXIT_CODE -eq 0 ]; then
  echo -e "${GREEN}✅ All tests passed!${NC}"
  echo ""
  echo "Test report: reports/test-report.html"
else
  echo -e "${RED}❌ Some tests failed${NC}"
  echo ""
  echo "Test report: reports/test-report.html"
  echo "Screenshots: test-results/ (if tests failed)"
  echo ""
  echo -e "${YELLOW}Debugging tips:${NC}"
  echo "  - Run with --headed to see browser"
  echo "  - Run with --slow to slow down operations"
  echo "  - Check service logs: docker compose logs"
fi

exit $TEST_EXIT_CODE
