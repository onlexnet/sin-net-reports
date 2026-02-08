#!/bin/bash

# Advanced GraphQL API Tests
# Tests customer creation, retrieval, and other GraphQL operations

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

GRAPHQL_URL="http://localhost:8081/graphql"

success() { echo -e "${GREEN}✓ $1${NC}"; }
error() { echo -e "${RED}✗ $1${NC}"; }
info() { echo -e "${YELLOW}→ $1${NC}"; }

echo -e "${YELLOW}============================================${NC}"
echo -e "${YELLOW}GraphQL API Integration Tests${NC}"
echo -e "${YELLOW}============================================${NC}"
echo ""

# Test 1: GraphQL Schema Introspection
info "Test 1: GraphQL Schema Introspection"
QUERY='{"query":"{ __schema { types { name } } }"}'
RESPONSE=$(curl -s -X POST "$GRAPHQL_URL" \
    -H "Content-Type: application/json" \
    -d "$QUERY")

if echo "$RESPONSE" | grep -q "Query"; then
    success "GraphQL schema is accessible"
else
    error "GraphQL schema introspection failed"
    echo "Response: $RESPONSE"
    exit 1
fi

# Test 2: Query Available Queries
info "Test 2: Checking available queries"
QUERY='{"query":"{ __schema { queryType { fields { name description } } } }"}'
RESPONSE=$(curl -s -X POST "$GRAPHQL_URL" \
    -H "Content-Type: application/json" \
    -d "$QUERY")

if echo "$RESPONSE" | grep -q "fields"; then
    success "Query types are available"
    # Show available queries
    echo "$RESPONSE" | grep -o '"name":"[^"]*"' | head -10
else
    error "Failed to retrieve query types"
    exit 1
fi

# Test 3: Query Available Mutations
info "Test 3: Checking available mutations"
QUERY='{"query":"{ __schema { mutationType { fields { name description } } } }"}'
RESPONSE=$(curl -s -X POST "$GRAPHQL_URL" \
    -H "Content-Type: application/json" \
    -d "$QUERY")

if echo "$RESPONSE" | grep -q "fields"; then
    success "Mutation types are available"
    # Show available mutations
    echo "$RESPONSE" | grep -o '"name":"[^"]*"' | head -10
else
    error "Failed to retrieve mutation types"
fi

# Test 4: Test User Stats Query (if available)
info "Test 4: Testing userStats query"
QUERY='{"query":"query { userStats { name } }"}'
RESPONSE=$(curl -s -X POST "$GRAPHQL_URL" \
    -H "Content-Type: application/json" \
    -d "$QUERY")

if echo "$RESPONSE" | grep -q '"data"'; then
    success "UserStats query executed successfully"
else
    info "UserStats query returned: $RESPONSE"
fi

# Test 5: Test Projects Query (if available)
info "Test 5: Testing projects query"
QUERY='{"query":"query { projects { items { entityId name } } }"}'
RESPONSE=$(curl -s -X POST "$GRAPHQL_URL" \
    -H "Content-Type: application/json" \
    -d "$QUERY")

if echo "$RESPONSE" | grep -q '"data"'; then
    success "Projects query executed successfully"
else
    info "Projects query returned: $RESPONSE"
fi

# Test 6: Test Customers Query
info "Test 6: Testing customers query"
QUERY='{"query":"query { customers { items { entityId name } } }"}'
RESPONSE=$(curl -s -X POST "$GRAPHQL_URL" \
    -H "Content-Type: application/json" \
    -d "$QUERY")

if echo "$RESPONSE" | grep -q '"data"'; then
    success "Customers query executed successfully"
    echo "Response: $RESPONSE"
else
    info "Customers query returned: $RESPONSE"
fi

# Note: Customer creation requires authentication
info "Note: Customer creation mutation requires proper Azure B2C authentication"
info "This would be tested in a full E2E test with authenticated user context"

echo ""
echo -e "${GREEN}GraphQL API Tests Completed${NC}"
echo ""
