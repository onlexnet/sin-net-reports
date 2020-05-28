import React from "react";

import ApolloClient from "apollo-boost";

import { AzureAD, AuthenticationState, IAzureADFunctionProps } from "react-aad-msal";
import { authProvider } from "./authProvider";

import { basicReduxStore } from "./reduxStore";

import { View as AuthenticatedView } from "./AppAuthenticated";
import { View as UnauthenticatedView } from "./AppUnauthenticated";
import { View as InProgressView } from "./AppInProgress";

const client = new ApolloClient({
  uri: "http://localhost:8080"
});

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
