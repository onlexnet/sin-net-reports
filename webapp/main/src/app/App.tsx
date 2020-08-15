import React from "react";

import { store, anyStore, RootState } from "../store/store";

import { View as AuthenticatedView } from "./AppAuthenticated";
import { View as UnauthenticatedView } from "./AppUnauthenticated";
import { View as InProgressView } from "./AppInProgress";
import { SessionState } from "../store/session/types";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";

const mapStateToProps = (state: RootState): SessionState => {
  return state.auth;
}
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    login: () => {
      alert('login');
    }
  }
}
const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface AppProps extends PropsFromRedux {
}



const App: React.FC<AppProps> = props => {
  return <UnauthenticatedView login={props.login} />;

  // return (
  //     {({ login, authenticationState, accountInfo }: IAzureADFunctionProps) => {
  //       var current: JSX.Element;
  //       if (authenticationState === AuthenticationState.Authenticated) {
  //         current = <AuthenticatedView authProvider={authProvider} accountInfo={accountInfo!} />;
  //       } else if (authenticationState === AuthenticationState.Unauthenticated) {
  //         current = <UnauthenticatedView login={login} />;
  //       } else {
  //         current = <InProgressView />
  //       }
  //       return current;
  //     }}
  // );
};

export default connector(App)
