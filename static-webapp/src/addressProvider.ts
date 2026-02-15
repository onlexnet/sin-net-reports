import { getRuntimeConfig } from "./app/configuration/RuntimeConfig";

export const addressProvider = (): { host: string } => {
  const { backendBaseUrl } = getRuntimeConfig();
  
  if (!backendBaseUrl) {
    throw new Error(
      'Backend URL is not configured. Ensure BACKEND_BASE_URL environment variable is set during container startup.'
    );
  }
  
  return { host: backendBaseUrl };
}
