import React, { useState, useEffect, Children, ReactNode } from "react";
import { PublicClientApplication, AuthError, AuthenticationResult } from "@azure/msal-browser";
import { AuthenticationState } from "./AuthenticationState";
import { Store } from "redux";
import { stat } from "fs";

type LoginFunction = () => void;
type LogoutFunction = () => void;

export interface IAzureADFunctionProps {
  state: AuthenticationState;
}

export interface IAzureADProps {
  provider: PublicClientApplication;
  children: (props: IAzureADFunctionProps) => React.ReactNode;
}

interface Account {
  idToken: string;
}

/**
 * Handles operations related to check progress of user's sign-in process
 * and renders proper view related th the process's stage.
 */
export const AzureAD: React.FC<IAzureADProps> = (props) => {
  const { provider } = props;
  const [state, setState] = useState<AuthenticationState>({ kind: "INITIAL" });
  const [account, setAccount] = useState<Account>();

  // auth.login();
  // const setStateUnauthenticated = () =>
  //   setState({
  //     kind: "UNAUTHENTICATED",
  //     login: async () => {
  //       // await provider.loginRedirect({
  //       //   redirectUri: 'http://localhost:3000/',
  //       //   scopes: ['openid', 'profile']
  //       // });
  //       auth.login();
  //     },

  //   });
  // const setStateAuthenticated = () =>
  //   setState({
  //     kind: "AUTHENTICATED",
  //     logout: () => provider.logout(),
  //   });

  // switch (state.kind) {
  //   case "INITIAL":
  //     if (account) setStateAuthenticated();
  //     else setStateUnauthenticated();
  //   case "AUTHENTICATED":
  //     if (!account) {
  //       setStateUnauthenticated();
  //     }
  //     break;
  //   case "UNAUTHENTICATED":
  //     if (account) {
  //       setStateAuthenticated();
  //     }
  //     break;
  // }

  return (
    <>
      {props.children({ state: state })}
    </>);
};
