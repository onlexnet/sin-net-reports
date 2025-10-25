# Runtime Configuration

This document explains how the application handles runtime configuration, particularly for sensitive values like Application Insights connection strings.

## Overview

The application uses a JSON-based configuration approach where sensitive configuration values are injected into the built JavaScript bundle during deployment. The configuration is defined in a static JSON file that gets bundled into the application and then replaced during deployment.

## Configuration File

### `/src/app/configuration/runtime-config.json`
This is the configuration template that contains default/placeholder values:

```json
{
  "applicationInsightsConnectionString": "",
  "environment": "development"
}
```

This file is imported directly in TypeScript and gets bundled into the main JavaScript file.

## How It Works

1. **Build Time**: The React app builds and bundles the `runtime-config.json` file into the main JS bundle
2. **Deployment Time**: The CI/CD pipeline uses `sed` to find and replace the JSON content in the JS bundle with environment-specific values
3. **Runtime**: The React app uses the injected values directly through the imported JSON

## Configuration Loading

The configuration is loaded through a direct JSON import in `src/app/configuration/RuntimeConfig.ts`:

```typescript
import runtimeConfigData from './runtime-config.json';

const runtimeConfig: RuntimeConfig = {
  ...runtimeConfigData,
  // Fallback to environment variables for local development
  applicationInsightsConnectionString: runtimeConfigData.applicationInsightsConnectionString || 
                                      process.env.REACT_APP_APPLICATIONINSIGHTS_CONNECTION_STRING || ''
};
```

## Benefits

- **Security**: No secrets in CI build artifacts
- **Environment Separation**: Each environment gets its own configuration injected at deployment
- **Simplicity**: Static JSON file that's easy to understand and modify
- **Performance**: No runtime HTTP requests - direct imports work immediately
- **Predictable**: The exact JSON structure is known and can be reliably replaced

## Local Development

For local development, use environment variables as the fallback:

```bash
export REACT_APP_APPLICATIONINSIGHTS_CONNECTION_STRING="your-connection-string"
npm start
```

The static JSON file will have empty values, so the environment variable fallback will be used.

## Deployment

The deployment process automatically:

1. Builds the React app with the template JSON content bundled in
2. Downloads the build artifacts
3. Uses `sed` to find and replace the JSON content in the JS bundle:
   ```bash
   # Replaces this exact string:
   {"applicationInsightsConnectionString":"","environment":"development"}
   
   # With environment-specific values:
   {"applicationInsightsConnectionString":"actual-secret","environment":"production"}
   ```
4. Deploys the configured application

## Technical Details

The deployment script:
- Finds the main JS bundle file (`main.*.js`)
- Uses `sed` with a precise JSON string replacement
- Replaces the entire JSON object in one operation
- Ensures the replacement is atomic and predictable

This approach combines the simplicity of static configuration with the security of deployment-time injection.