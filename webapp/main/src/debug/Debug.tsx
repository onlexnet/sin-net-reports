import { useIsAuthenticated, useMsal } from "@azure/msal-react";
import { DefaultButton } from "@fluentui/react";
import React from "react";


interface Props {
}

export const Debug: React.FC<Props> = () => {
  const a = useMsal();
  const isAuthenticated = useIsAuthenticated();

  return (
    <>
      <p><small>You are running this application in <b>{process.env.NODE_ENV}</b> mode.</small></p>
      {isAuthenticated && <DefaultButton onClick={() => a.instance.logout()}>Logout</DefaultButton>}
    </>
  );
};
