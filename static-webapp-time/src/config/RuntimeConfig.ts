export interface RuntimeConfig {
  applicationInsightsConnectionString: string;
  backendBaseUrl: string;
  environment: string;
  useTestLogin: boolean;
}

let cachedRuntimeConfig: RuntimeConfig | null = null;

export const loadRuntimeConfig = async (): Promise<RuntimeConfig> => {
  if (cachedRuntimeConfig) {
    return cachedRuntimeConfig;
  }

  const response = await fetch('/runtime-config.json');
  if (!response.ok) {
    throw new Error(`Failed to load runtime-config.json: ${response.status} ${response.statusText}.`);
  }

  const config: RuntimeConfig = await response.json();
  cachedRuntimeConfig = config;
  return config;
};

export const getRuntimeConfig = (): RuntimeConfig => {
  if (!cachedRuntimeConfig) {
    throw new Error('Runtime configuration not loaded. Call loadRuntimeConfig() first.');
  }
  return cachedRuntimeConfig;
};
