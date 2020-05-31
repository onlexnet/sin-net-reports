export const addressProvider = (): { host: string } => {
  switch (window.location.origin) {
    case "https://raport.sin.net.pl":
      return {
        host: "https://raport.sin.net.pl",
      };
    default:
      return {
        host: "http://localhost:8080",
      };
  }
};
