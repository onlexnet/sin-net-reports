#!/bin/bash

# Build script for uservice-webapi that automatically builds shared dependencies
# This replaces the manual 3-step build process with a single command

set -e  # Exit on any error

echo "Building shared dependencies for uservice-webapi..."

# Build api/client-java first
echo "Building api/client-java..."
mvn -f ../api/client-java install -ntp

# Build libs-java second  
echo "Building libs-java..."
mvn -f ../libs-java install -ntp

# Finally build this service (skip tests by default as they require external services)
echo "Building uservice-webapi..."
mvn clean install -ntp -DskipTests "$@"

echo "Build completed successfully!"