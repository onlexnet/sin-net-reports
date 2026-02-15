# Multi-stage build for static-webapp
# Stage 1: Build the React application
FROM node:22.12.0-bookworm AS build

WORKDIR /app

# Copy GraphQL schema files from webapi (needed for codegen)
COPY uservice-webapi/host/src/main/resources/graphql uservice-webapi/host/src/main/resources/graphql

# Copy package files
COPY static-webapp/package*.json ./
COPY static-webapp/.version ./

# Install dependencies
RUN --mount=type=cache,target=/root/.npm \
    npm ci

# Copy source code and config files
COPY static-webapp/graphql-codegen-config.yml ./
COPY static-webapp/tsconfig.json ./
COPY static-webapp/public ./public
COPY static-webapp/src ./src

# Generate GraphQL models and build the app
RUN npm run generate && \
    export MY_VERSION=$(cat .version) && \
    npm config set allow-same-version true && \
    npm version $MY_VERSION && \
    npm run build

# Stage 2: Configure and serve with Node.js
FROM node:22.12.0-alpine

# Build-time configuration arguments
ARG BACKEND_BASE_URL
ARG APPLICATIONINSIGHTS_CONNECTION_STRING=""
ARG ENVIRONMENT="development"
ARG USE_TEST_LOGIN

WORKDIR /app

# Copy built React app from build stage
COPY --from=build /app/build ./build

# Create a non-root user and switch to it
RUN addgroup -g 1001 -S nodejs && \
    adduser -S nodejs -u 1001

# Validate required build arguments
RUN if [ -z "$BACKEND_BASE_URL" ]; then \
      echo "ERROR: BACKEND_BASE_URL build argument is required" && \
      exit 1; \
    fi

# Validate that runtime-config.json exists in build
RUN if [ ! -f "build/runtime-config.json" ]; then \
      echo "ERROR: runtime-config.json not found in build directory" && \
      exit 1; \
    fi

# Replace runtime configuration in the JSON file
RUN sed -i 's|"applicationInsightsConnectionString": ""|"applicationInsightsConnectionString": "'"$APPLICATIONINSIGHTS_CONNECTION_STRING"'"|g' build/runtime-config.json && \
    sed -i 's|"backendBaseUrl": ""|"backendBaseUrl": "'"$BACKEND_BASE_URL"'"|g' build/runtime-config.json && \
    sed -i 's|"environment": "development"|"environment": "'"$ENVIRONMENT"'"|g' build/runtime-config.json && \
    sed -i 's|"useTestLogin": false|"useTestLogin": '"$USE_TEST_LOGIN"'|g' build/runtime-config.json && \
    echo "Configuration injected into runtime-config.json:" && \
    cat build/runtime-config.json

# Install serve globally for serving static files
RUN npm install -g serve

# Set proper ownership of the /app directory
RUN chown -R nodejs:nodejs /app

# Switch to non-root user
USER nodejs

# Expose port 3000
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:3000/health || exit 1

# Serve the built app with SPA routing support
CMD ["serve", "-s", "build", "-l", "3000"]
