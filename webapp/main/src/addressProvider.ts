export const addressProvider = (): { host: string } => {
  const origin = window.location.origin;
  return (origin.endsWith("raport.sin.net.pl"))
    ? {
        host: origin,
      }
    : {
        host: "http://localhost:8080",
      };
  }
