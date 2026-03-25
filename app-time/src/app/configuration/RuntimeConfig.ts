/**
 * Runtime configuration interface
 */
export interface RuntimeConfig {
  applicationInsightsConnectionString: string;
  backendBaseUrl: string;
  environment: string;
  useTestLogin: boolean;
}

/**
 * Cached runtime configuration
 */
let cachedRuntimeConfig: RuntimeConfig | null = null;

/**
 * Loads runtime configuration from /runtime-config.json
 * This file is required and must be configured during Docker build or GitHub Actions deployment
 * 
 * @returns Promise with RuntimeConfig
 * @throws Error if runtime-config.json is missing or invalid
 */
export const loadRuntimeConfig = async (): Promise<RuntimeConfig> => {
  if (cachedRuntimeConfig) {
    return cachedRuntimeConfig;
  }

  const response = await fetch('/runtime-config.json');
  if (!response.ok) {
    throw new Error(`Failed to load runtime-config.json: ${response.status}. Ensure the file is configured during container startup.`);
  }
  
  const config: RuntimeConfig = await response.json();
  cachedRuntimeConfig = config;
  return config;
};

/**
 * Gets the runtime configuration (synchronous)
 * Must call loadRuntimeConfig() first during app initialization
 * 
 * @returns The runtime configuration
 * @throws Error if configuration hasn't been loaded yet
 */
export const getRuntimeConfig = (): RuntimeConfig => {
  if (!cachedRuntimeConfig) {
    throw new Error('Runtime configuration not loaded. Call loadRuntimeConfig() first.');
  }
  return cachedRuntimeConfig;
};