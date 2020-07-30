import React from "react";

import { AzureAD, AuthenticationState, IAzureADFunctionProps } from "react-aad-msal";
import { authProvider } from "./authProvider";

import { basicReduxStore } from "./reduxStore";

import { View as AuthenticatedView } from "./app/AppAuthenticated";
import { View as UnauthenticatedView } from "./app/AppUnauthenticated";
import { View as InProgressView } from "./app/AppInProgress";

export const App: React.FC<{}> = () => {
  const [login, setLogin] = React.useState();

  return (
    <AzureAD provider={authProvider} reduxStore={basicReduxStore}>
      {({ login, authenticationState, accountInfo }: IAzureADFunctionProps) => {
        var current: JSX.Element;
        if (authenticationState === AuthenticationState.Authenticated) {
          current = <AuthenticatedView authProvider={authProvider} accountInfo={accountInfo!} />;
        } else if (authenticationState === AuthenticationState.Unauthenticated) {
          current = <UnauthenticatedView login={login} />;
        } else {
          current = <InProgressView />
        }
        return current;
      }}
    </AzureAD>
  );
};
