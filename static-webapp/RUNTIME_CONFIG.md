# Runtime Configuration

This document explains how the application handles runtime configuration, particularly for sensitive values like Application Insights connection strings.

## Overview

The application uses a **unified build-time injection approach** where:
1. Configuration is stored in `/runtime-config.json` (served as a static file)
2. The React app **fetches** this file during initialization
3. Values in the JSON file are injected using `sed` during build/deployment:
   - **Docker builds** (smoke tests): Configuration injected during image build via Dockerfile ARGs
   - **GitHub Actions** (production): Configuration injected after build, before Azure deployment

This approach provides a clean separation between the application bundle and configuration.

## Configuration File

### `/public/runtime-config.json`
Template file with placeholder values (copied to build during `npm run build`):

```json
{
  "applicationInsightsConnectionString": "",
  "backendBaseUrl": "",
  "environment": "development"
}
```

This file is:
- Copied from `public/` to `build/` during React build
- Served as a static file at `/runtime-config.json`
- Fetched by the application during initialization
- Modified by `sed` during Docker build or GitHub Actions deployment

## How It Works

1. **Build Time**: The React app builds and copies `runtime-config.json` from `public/` to `build/` with placeholder values
2. **Injection Time**: `sed` command replaces values directly in the JSON file
   - **Docker**: During `docker build` using ARG variables  
   - **GitHub Actions**: After build, before deployment to Azure Static Web Apps
3. **Runtime**: The React app fetches `/runtime-config.json` during initialization and caches the configuration

## Configuration Loading

The configuration is loaded asynchronously in `src/app/configuration/RuntimeConfig.ts`:

```typescript
// Fetch and cache configuration during app initialization
export const loadRuntimeConfig = async (): Promise<RuntimeConfig> => {
  const response = await fetch('/runtime-config.json');
  if (!response.ok) {
    throw new Error(`Failed to load runtime-config.json: ${response.status}. Ensure the file is configured during container startup.`);
  }
  const config: RuntimeConfig = await response.json();
  return config;
};

// Get cached configuration (synchronous)
export const getRuntimeConfig = (): RuntimeConfig => {
  return cachedConfig;
};
```

The application initializes configuration in `src/index.tsx` before rendering:

```typescript
const initializeApp = async () => {
  // Load runtime configuration first (required before app starts)
  await loadRuntimeConfig();
  
  // Initialize Application Insights
  initializeApplicationInsights();
  
  // Render the application
  ReactDOM.render(<App />, document.getElementById("app"));
};

initializeApp();
```

This ensures configuration is properly loaded before any component tries to use it. If `/runtime-config.json` is missing or invalid, the application will fail fast with a clear error message.

## Benefits

- **Clean Separation**: Configuration is a separate file, not embedded in JavaScript bundles
- **Easy Inspection**: Runtime configuration is visible at `/runtime-config.json` endpoint
- **Consistency**: Same injection mechanism for all scenarios (Docker, GitHub Actions, local builds)
- **Security**: No secrets in source code or base Docker images
- **Environment Separation**: Each environment gets its own configuration injected at build time
- **Simplicity**: No runtime scripts or entrypoints - pure static file serving
- **Debuggable**: Can easily check configuration by fetching `/runtime-config.json`
- **Predictable**: Standard JSON file format with standard `sed` replacements

## Local Development

### With npm start
For local development with React dev server, the app requires `/runtime-config.json` to be configured. You can serve this file from the public directory:

1. Update `public/runtime-config.json` with your local configuration:
```json
{
  "applicationInsightsConnectionString": "",
  "backendBaseUrl": "http://localhost:11031",
  "environment": "development",
  "useTestLogin": true
}
```

2. Run the dev server:
```bash
npm start
```

The React dev server will serve the configuration file from the `public/` directory.

### With Docker (smoke tests)
For Docker-based local development, configuration is provided via `docker-compose.yml`:

```yaml
build:
  args:
    - BACKEND_BASE_URL=http://localhost:11031
    - APPLICATIONINSIGHTS_CONNECTION_STRING=
    - ENVIRONMENT=development
```

The Dockerfile will inject these values into `/runtime-config.json` during build.

## Deployment Scenarios

### Docker Build (Smoke Tests)

The Dockerfile uses build-time ARG variables and `sed` to inject configuration into the JSON file:

```dockerfile
ARG BACKEND_BASE_URL
ARG APPLICATIONINSIGHTS_CONNECTION_STRING=""
ARG ENVIRONMENT="development"

# Validate that runtime-config.json exists in build
RUN if [ ! -f "build/runtime-config.json" ]; then \
      echo "ERROR: runtime-config.json not found in build directory" && \
      exit 1; \
    fi

# Replace runtime configuration in the JSON file
RUN sed -i 's|"applicationInsightsConnectionString": ""|"applicationInsightsConnectionString": "'"$APPLICATIONINSIGHTS_CONNECTION_STRING"'"|g' build/runtime-config.json && \
    sed -i 's|"backendBaseUrl": ""|"backendBaseUrl": "'"$BACKEND_BASE_URL"'"|g' build/runtime-config.json && \
    sed -i 's|"environment": "development"|"environment": "'"$ENVIRONMENT"'"|g' build/runtime-config.json
```

Values are provided in `docker-compose.yml`:

```yaml
build:
  args:
    - BACKEND_BASE_URL=http://localhost:11031
    - APPLICATIONINSIGHTS_CONNECTION_STRING=
    - ENVIRONMENT=development
```

### GitHub Actions (Azure Static Web Apps)

The deployment workflow injects configuration directly into the JSON file after build:

1. Builds the React app with template JSON file
2. Downloads the build artifacts
3. Uses `sed` to replace values in `runtime-config.json`:
   ```bash
   # Replace configuration values in runtime-config.json
   sed -i 's|"applicationInsightsConnectionString": ""|"applicationInsightsConnectionString": "'"$APPLICATIONINSIGHTS_CONNECTION_STRING"'"|g' runtime-config.json
   sed -i 's|"backendBaseUrl": ""|"backendBaseUrl": "'"$BACKEND_BASE_URL"'"|g' runtime-config.json
   sed -i 's|"environment": "development"|"environment": "production"|g' runtime-config.json
   ```
4. Deploys the configured application to Azure Static Web Apps

## Technical Details

The injection mechanism:
- Validates that `runtime-config.json` exists in the build directory
- Uses `sed` with precise string replacement on individual JSON properties
- Replaces each configuration value in separate operations for clarity
- Works identically in Docker builds and CI/CD pipelines

**Important**: The placeholder values in the JSON must match exactly:
```json
{
  "applicationInsightsConnectionString": "",
  "backendBaseUrl": "",
  "environment": "development"
}
```

Any changes to property names or structure require updates to:
- `public/runtime-config.json` (template)
- `RuntimeConfig.ts` interface
- Dockerfile sed commands
- GitHub Actions workflow sed commands

This unified approach eliminates the need for:
- Runtime entrypoint scripts
- Bundling configuration into JavaScript
- Complex configuration management systems
- Searching for configuration in minified JS bundles

The configuration is always available as a clean, inspectable JSON file at `/runtime-config.json`.