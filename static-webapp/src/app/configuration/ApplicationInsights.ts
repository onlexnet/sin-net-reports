import { ApplicationInsights } from '@microsoft/applicationinsights-web';

let appInsights: ApplicationInsights | null = null;

/**
 * Initializes Application Insights
 * @returns The Application Insights instance
 */
export const initializeApplicationInsights = () => {
  const connectionString = process.env.REACT_APP_APPLICATIONINSIGHTS_CONNECTION_STRING;
  
  if (!connectionString) {
    console.warn('Application Insights connection string not configured. Telemetry will not be collected.');
    return { appInsights: null };
  }

  appInsights = new ApplicationInsights({
    config: {
      connectionString: connectionString,
      enableAutoRouteTracking: true,
    }
  });

  appInsights.loadAppInsights();
  appInsights.trackPageView();

  return { appInsights };
};

/**
 * Gets the initialized Application Insights instance
 * @returns The Application Insights instance or null if not initialized
 */
export const getAppInsights = (): ApplicationInsights | null => {
  return appInsights;
};

/**
 * Sets the authenticated user context
 * @param userId The user's email or identifier
 */
export const setAuthenticatedUser = (userId: string) => {
  if (appInsights) {
    appInsights.setAuthenticatedUserContext(userId);
    console.log('Application Insights: Authenticated user context set to', userId);
  }
};

/**
 * Clears the authenticated user context
 */
export const clearAuthenticatedUser = () => {
  if (appInsights) {
    appInsights.clearAuthenticatedUserContext();
    console.log('Application Insights: Authenticated user context cleared');
  }
};
