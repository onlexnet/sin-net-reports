#!/bin/bash

# Example script showing how to add test data to the database
# This can be customized for specific test scenarios

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

success() { echo -e "${GREEN}✓ $1${NC}"; }
error() { echo -e "${RED}✗ $1${NC}"; }
info() { echo -e "${YELLOW}→ $1${NC}"; }

GRAPHQL_URL="http://localhost:8081/graphql"

echo -e "${YELLOW}============================================${NC}"
echo -e "${YELLOW}Adding Test Data${NC}"
echo -e "${YELLOW}============================================${NC}"
echo ""

info "This is an example script showing how to add test data"
info "Customize this script for your specific testing needs"
echo ""

# Example 1: Create a test customer
info "Example: Creating a test customer via GraphQL"
cat <<'EOF'

# GraphQL Mutation for creating a customer:
mutation CreateCustomer {
  customerCreate(
    command: {
      name: "Test Customer"
      address: "123 Test Street"
      city: "Test City"
      postalCode: "12345"
      nip: "1234567890"
    }
  ) {
    entityId
    name
  }
}

# Execute with curl:
curl -X POST http://localhost:8081/graphql \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "query": "mutation { customerCreate(command: { name: \"Test Customer\", address: \"123 Test Street\", city: \"Test City\", postalCode: \"12345\", nip: \"1234567890\" }) { entityId name } }"
  }'

EOF

# Example 2: Create test time entries
info "Example: Creating test time entries"
cat <<'EOF'

# GraphQL Mutation for creating a time entry:
mutation CreateTimeEntry {
  actionCreate(
    command: {
      date: "2024-01-15"
      description: "Test work"
      distance: 0
      duration: 480
      customerId: "customer-uuid-here"
    }
  ) {
    entityId
    description
  }
}

EOF

# Example 3: Direct SQL insert (if needed)
info "Example: Direct database insert via SQL"
cat <<'EOF'

# SQL statements can be executed directly:
docker exec smoke-test-sqlserver /opt/mssql-tools/bin/sqlcmd \
  -S localhost -U sa -P "YourStrong@Passw0rd" -d sinnet \
  -Q "INSERT INTO customers (id, name, address, city) VALUES (NEWID(), 'Test Customer', '123 Test St', 'Test City')"

EOF

info ""
info "Note: Most mutations require authentication via Azure B2C"
info "For authenticated tests, you'll need to:"
info "  1. Obtain a valid JWT token from Azure B2C"
info "  2. Include the token in the Authorization header"
info "  3. Use the token in your GraphQL mutations"
echo ""

success "Test data examples displayed"
info "Customize this script for your specific test scenarios"
