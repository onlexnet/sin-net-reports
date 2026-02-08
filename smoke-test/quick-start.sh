#!/bin/bash

# Quick start script - builds and runs smoke tests
# This is a convenience script that runs all necessary build steps

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

success() { echo -e "${GREEN}✓ $1${NC}"; }
error() { echo -e "${RED}✗ $1${NC}"; }
info() { echo -e "${YELLOW}→ $1${NC}"; }

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo -e "${YELLOW}============================================${NC}"
echo -e "${YELLOW}SinNet Reports - Quick Start${NC}"
echo -e "${YELLOW}Building and Running Smoke Tests${NC}"
echo -e "${YELLOW}============================================${NC}"
echo ""

# Check prerequisites
info "Checking prerequisites..."

if ! command -v java &> /dev/null; then
    error "Java is not installed or not in PATH"
    exit 1
fi

if ! command -v mvn &> /dev/null; then
    error "Maven is not installed or not in PATH"
    exit 1
fi

if ! command -v node &> /dev/null; then
    error "Node.js is not installed or not in PATH"
    exit 1
fi

if ! command -v docker &> /dev/null; then
    error "Docker is not installed or not in PATH"
    exit 1
fi

success "All prerequisites are met"

# Set Java environment
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Build API client
info "Building API client libraries..."
cd "$PROJECT_ROOT/api/client-java"
mvn clean install -DskipTests -q
success "API client built"

# Build timeentries service
info "Building timeentries service..."
cd "$PROJECT_ROOT/uservice-timeentries"
export SEMVERSION=$(cat .version)
mvn -ntp install -pl host -am -DskipTests
success "Timeentries service built"

# Build webapi service
info "Building webapi service..."
cd "$PROJECT_ROOT/uservice-webapi"
export SEMVERSION=$(cat .semversion)
mvn -ntp install -Drevision=$SEMVERSION -DskipTests
success "WebAPI service built"

# Build frontend
info "Building frontend..."
cd "$PROJECT_ROOT/static-webapp"

# Setup Node.js version
export NVM_DIR="$HOME/.nvm"
if [ -s "$NVM_DIR/nvm.sh" ]; then
    \. "$NVM_DIR/nvm.sh"
    nvm use 22.12.0 2>/dev/null || true
fi

npm install --silent
npm run generate
npm run build
success "Frontend built"

# Run smoke tests
info "Starting smoke tests..."
cd "$SCRIPT_DIR"
exec ./smoke-test.sh "$@"
