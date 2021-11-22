export const addressProvider = (): { host: string } => {
  const origin = window.location.origin;
  return (origin.endsWith("raport.sin.net.pl"))
    ? {
        host: origin,
      }
    : {
        host: "https://beta.raport.sin.net.pl",
      };
  }
