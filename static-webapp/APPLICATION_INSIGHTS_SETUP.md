# Application Insights Setup for React WebApp

## Overview
Application Insights has been integrated into the React webapp to provide telemetry and user tracking capabilities. This enables monitoring of operations per user and provides insights into application usage.

## Implementation Details

### Changes Made

1. **Added Application Insights Package**
   - Installed `@microsoft/applicationinsights-web@3.3.4`
   - Package is automatically included during npm install

2. **Created Configuration Module**
   - Location: `static-webapp/src/app/configuration/ApplicationInsights.ts`
   - Exports functions to:
     - Initialize Application Insights
     - Set authenticated user context
     - Clear authenticated user context

3. **Initialization**
   - Application Insights is initialized in `static-webapp/src/index.tsx`
   - Reads connection string from `REACT_APP_APPLICATIONINSIGHTS_CONNECTION_STRING` environment variable
   - If connection string is not configured, a warning is logged and telemetry is disabled

4. **User Tracking**
   - When a user logs in via Azure B2C, their email is set as the authenticated user context
   - Implementation in `static-webapp/src/app/AppInProgress.tsx`
   - When a user logs out, the authenticated user context is cleared
   - Implementation in `static-webapp/src/debug/Debug.tsx`

5. **Build Configuration**
   - GitHub workflow updated to pass `REACT_APP_APPLICATIONINSIGHTS_CONNECTION_STRING` during build
   - Location: `.github/workflows/static-webapp.yml`

## Configuration Required

### GitHub Secrets
Add the following secret to the GitHub repository:
- **Secret Name**: `APPLICATIONINSIGHTS_CONNECTION_STRING`
- **Secret Value**: The connection string from Azure Application Insights resource

To find the connection string:
1. Navigate to Azure Portal
2. Open the Application Insights resource (should already exist based on Terraform config)
3. Go to "Overview" or "Properties"
4. Copy the "Connection String" (format: `InstrumentationKey=...;IngestionEndpoint=...`)

### Setting the Secret
```bash
# Via GitHub UI:
# 1. Go to repository Settings > Secrets and variables > Actions
# 2. Click "New repository secret"
# 3. Name: APPLICATIONINSIGHTS_CONNECTION_STRING
# 4. Value: <paste connection string>
# 5. Click "Add secret"
```

### Local Development
For local development, create a `.env.local` file in the `static-webapp` directory:
```
REACT_APP_APPLICATIONINSIGHTS_CONNECTION_STRING=InstrumentationKey=...;IngestionEndpoint=...
```

**Note**: `.env.local` is already in `.gitignore` and will not be committed.

## Testing

### Verify Setup
1. After deploying, check browser console for Application Insights messages
2. Log in as a user and verify the authenticated user context is set (check console logs)
3. Navigate to Azure Portal > Application Insights > Users to see tracked users
4. Check "Live Metrics" to see real-time telemetry

### Expected Behavior
- **Without connection string**: Warning logged to console, no telemetry sent
- **With connection string**: 
  - Page views tracked automatically
  - User email tracked after login
  - User context cleared on logout
  - Route changes tracked automatically

## Features Enabled

1. **Automatic Page View Tracking**: Every page navigation is tracked
2. **Automatic Route Tracking**: SPA route changes are tracked
3. **Authenticated User Context**: User email is tracked with all telemetry
4. **Exception Tracking**: Unhandled exceptions are automatically tracked
5. **Performance Monitoring**: Page load times and AJAX calls are tracked

## Monitoring Operations Per User

Once deployed, you can view operations per user in Azure Portal:

1. Navigate to Application Insights resource
2. Go to "Users" to see user engagement
3. Go to "User Flows" to see user navigation patterns
4. Use "Analytics" (Logs) to query by authenticated user:
   ```kusto
   requests
   | where user_AuthenticatedId != ""
   | summarize count() by user_AuthenticatedId
   | order by count_ desc
   ```

## Troubleshooting

**Issue**: "Application Insights connection string not configured" warning in console
- **Solution**: Verify the GitHub secret is set correctly and the workflow has access to it

**Issue**: No telemetry appearing in Azure Portal
- **Solution**: 
  - Check the connection string is correct
  - Verify Application Insights resource is active
  - Wait 1-2 minutes for telemetry to appear (there's a delay)
  - Check browser network tab for requests to `dc.services.visualstudio.com`

**Issue**: User email not appearing in telemetry
- **Solution**: Verify the user is successfully logging in through Azure B2C and the email is being passed correctly

## Additional Resources
- [Application Insights JavaScript SDK Documentation](https://learn.microsoft.com/en-us/azure/azure-monitor/app/javascript)
- [Application Insights React Documentation](https://learn.microsoft.com/en-us/azure/azure-monitor/app/javascript-react-plugin)
