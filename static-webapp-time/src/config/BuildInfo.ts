export const getDisplayVersion = (): string => {
  const buildVersion = import.meta.env.VITE_BUILD_VERSION;

  if (buildVersion) {
    return buildVersion as string;
  }

  if (import.meta.env.DEV) {
    return '1.0.0-dev';
  }

  return '1.0.0';
};
