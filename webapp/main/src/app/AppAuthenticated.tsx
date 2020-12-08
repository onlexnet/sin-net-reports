import React, { useEffect } from "react";
import { Main } from "../services";
import { Customers } from "./customers/Customers";
import { Reports } from "../reports/Reports";
import { Home } from "../Home";

import { routing } from "../Routing";
import { NavBasicExample } from "../NavBar";
import { CustomerView } from "./customer/CustomerView";
import { Debug } from "../debug/Debug";
import { ApolloProvider } from "@apollo/react-hooks";
import { apolloClientFactory } from "../api";
import { RootState } from "../store/reducers";
import { SessionState } from "../store/session/types";
import { Dispatch } from "redux";
import { connect, ConnectedProps } from "react-redux";
import { MainView } from "./mainview/MainView";

const mapStateToProps = (state: RootState): SessionState => {
  return state.auth;
}
const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
  }
}
const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;



interface Props extends PropsFromRedux {
}

const ViewLocal: React.FC<Props> = (props) => {

  useEffect(() => {

  }, []);

  var client = apolloClientFactory(props.idToken);

  return (
    <ApolloProvider client={client}>
      <MainView />
    </ApolloProvider>
  );
};

export const View = connect(mapStateToProps, mapDispatchToProps)(ViewLocal);