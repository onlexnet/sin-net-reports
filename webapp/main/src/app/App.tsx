import React from "react";

import { View as AuthenticatedView } from "./AppAuthenticated";
import { View as UnauthenticatedView } from "./AppUnauthenticated";
import { View as InProgressView } from "./AppInProgress";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import { SessionState, SignInFlow } from "../store/session/types";
import { initiateSession } from "../store/session/actions";
import { RootState } from "../store/reducers";

const mapStateToProps = (state: RootState): SessionState => {
  return state.auth;
}
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    login: () => {
      dispatch(initiateSession());
    }
  }
}
const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface AppProps extends PropsFromRedux {
}

const App: React.FC<AppProps> = props => {

  var current: JSX.Element;
  switch (props.flow) {
    case SignInFlow.Unknown:
      current = <UnauthenticatedView login={props.login} />;
      break;
    case SignInFlow.SessionInitiated:
      current = <InProgressView />;
      break;
    case SignInFlow.SessionEstablished:
      current = <AuthenticatedView />;
      break;
    default:
      current = <UnauthenticatedView login={props.login} />;
  }
  return current;
};

export default connector(App)
