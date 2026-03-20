import packageJson from '../../../package.json';

export interface BuildInfo {
  version: string;
  buildVersion?: string;
}

export const getBuildInfo = (): BuildInfo => {
  // Get build version from environment variable (set during CI build)
  const buildVersion = process.env.REACT_APP_BUILD_VERSION;
  
  return {
    version: packageJson.version,
    buildVersion: buildVersion
  };
};

export const getDisplayVersion = (): string => {
  const buildInfo = getBuildInfo();
  
  if (buildInfo.buildVersion) {
    return buildInfo.buildVersion;
  }
  
  // For local development, show package version with dev indicator
  if (process.env.NODE_ENV === 'development') {
    return `${buildInfo.version}-dev`;
  }
  
  return buildInfo.version;
};
