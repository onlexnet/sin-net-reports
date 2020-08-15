import React from "react";

import { AzureAD, AuthenticationState, IAzureADFunctionProps } from "react-aad-msal";
import { authProvider } from "../authProvider";

import { store, anyStore } from "../reduxStore";

import { View as AuthenticatedView } from "./AppAuthenticated";
import { View as UnauthenticatedView } from "./AppUnauthenticated";
import { View as InProgressView } from "./AppInProgress";

export const App: React.FC<{}> = () => {
  const [login, setLogin] = React.useState();

  return (
    <AzureAD provider={authProvider} reduxStore={anyStore}>
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
