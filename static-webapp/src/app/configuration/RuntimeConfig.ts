import runtimeConfigData from './runtime-config.json';

/**
 * Runtime configuration interface
 */
export interface RuntimeConfig {
  applicationInsightsConnectionString: string;
  environment: string;
}

/**
 * Runtime configuration loaded from JSON file
 * This file will be replaced during deployment with environment-specific values
 */
const runtimeConfig: RuntimeConfig = {
  ...runtimeConfigData,
  // Fallback to environment variables for local development
  applicationInsightsConnectionString: runtimeConfigData.applicationInsightsConnectionString || 
                                      process.env.REACT_APP_APPLICATIONINSIGHTS_CONNECTION_STRING || '',
  environment: runtimeConfigData.environment || process.env.NODE_ENV || 'development'
};

/**
 * Gets the runtime configuration
 * @returns The runtime configuration
 */
export const getRuntimeConfig = (): RuntimeConfig => {
  return runtimeConfig;
};