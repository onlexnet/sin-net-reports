export const addressProvider = (): { host: string } => {
  const origin = window.location.origin;
  const host = asBackendHost(origin);
  return { host };
}

const asBackendHost = (appHost: string): string => {
  const dev01 = "https://sinnet-webapi.proudstone-d1ff7602.westeurope.azurecontainerapps.io";
  console.log("origin", origin);

  if (origin.endsWith("raport.sin.net.pl")) return appHost;
  if (origin.includes("localhost")) return dev01;
  if (origin.endsWith("sinnet.local")) return appHost;
  if (origin.endsWith("sinnet-dev01.onlex.net")) return dev01;
  return "https://sinnet.local";
  // return "http://localhost:11010";
}
