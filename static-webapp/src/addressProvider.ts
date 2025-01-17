export const addressProvider = (): { host: string } => {
  const origin = window.location.origin;
  const host = asBackendHost(origin);
  return { host };
}

const asBackendHost = (appHost: string): string => {
  const prd01 = "https://sinnet-webapi.wonderfulriver-536309a1.westeurope.azurecontainerapps.io";

  console.log("origin", origin);

  if (origin.endsWith("raport.sin.net.pl")) return appHost;
  if (origin.includes("localhost")) return prd01;
  if (origin.endsWith("sinnet.onlex.net")) return prd01;
  if (origin.endsWith("sinnet-test.onlex.net")) return prd01;
  return "https://sinnet.local";
}
