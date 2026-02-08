#!/bin/bash

# Database initialization script for smoke tests
# Creates the sinnet database and required tables

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

success() { echo -e "${GREEN}✓ $1${NC}"; }
error() { echo -e "${RED}✗ $1${NC}"; }
info() { echo -e "${YELLOW}→ $1${NC}"; }

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-1433}"
DB_USER="${DB_USER:-sa}"
DB_PASSWORD="${DB_PASSWORD:-YourStrong@Passw0rd}"
DB_NAME="sinnet"

info "Initializing database: $DB_NAME"
info "Database host: $DB_HOST:$DB_PORT"

# Wait for SQL Server to be ready
info "Waiting for SQL Server to be ready..."
MAX_RETRIES=30
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if docker exec smoke-test-sqlserver /opt/mssql-tools/bin/sqlcmd \
        -S localhost -U "$DB_USER" -P "$DB_PASSWORD" -Q "SELECT 1" > /dev/null 2>&1; then
        success "SQL Server is ready"
        break
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo -n "."
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    error "SQL Server failed to start"
    exit 1
fi

# Create database
info "Creating database: $DB_NAME"
docker exec smoke-test-sqlserver /opt/mssql-tools/bin/sqlcmd \
    -S localhost -U "$DB_USER" -P "$DB_PASSWORD" \
    -Q "IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = '$DB_NAME') CREATE DATABASE $DB_NAME" || {
    error "Failed to create database"
    exit 1
}

success "Database initialization completed"

# Note: Tables will be created automatically by Spring Boot on first service startup
info "Tables will be created automatically by Spring Boot JPA"
