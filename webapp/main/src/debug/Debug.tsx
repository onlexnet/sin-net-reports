import { useIsAuthenticated, useMsal } from "@azure/msal-react";
import { DefaultButton } from "@fluentui/react";
import React from "react";
import { useUserContextQuery } from '../Components/.generated/components'


interface Props {
}

export const Debug: React.FC<Props> = () => {
  // const client = apolloClientFactory(accountInfo.jwtIdToken);
  // const { data, loading, error } = useUserContextQuery({ client });
  const { data, loading, error } = useUserContextQuery();
  const a = useMsal();
  const isAuthenticated = useIsAuthenticated();

  return (
    <>
      <p>data: {String(data)}</p>
      <p>loading: {String(loading)}</p>
      <p>error: {String(error)}</p>
      <p><small>You are running this application in <b>{process.env.NODE_ENV}</b> mode.</small></p>

      {isAuthenticated && <DefaultButton onClick={() => a.instance.logout()}>Logout</DefaultButton>}
      <p>DEBUG</p>
    </>
  );
};
