export const addressProvider = (): { host: string } => {
  return (window.location.origin === "https://raport.sin.net.pl")
    ? {
        host: "https://raport.sin.net.pl",
      }
    : {
        host: "http://localhost:8080",
      };
  }
};
