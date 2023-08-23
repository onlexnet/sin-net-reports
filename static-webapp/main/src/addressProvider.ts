export const addressProvider = (): { host: string } => {
  const origin = window.location.origin;
  const host = asBackendHost(origin);
  return { host };
}

const asBackendHost = (appHost: string): string => {
  if (origin.endsWith("raport.sin.net.pl")) return appHost;
  console.log("origin", origin);
  if (origin.endsWith("sinnet.local")) return appHost;
  if (origin.endsWith("sinnet-dev01.onlex.net")) return "https://sinnet-webapi--f0l0su8.proudstone-d1ff7602.westeurope.azurecontainerapps.io/";
  return "https://sinnet.local";
  // return "http://localhost:11010";
}
