import React, { useEffect } from "react";
import { HashRouter as Router, Route } from "react-router-dom";
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

      <Router>
        <div className="ms-Grid" dir="ltr">
          <div className="ms-Grid-row">
            <div className="ms-Grid-col ms-sm6 ms-md4 ms-lg2">
              <Route path="/" component={NavBasicExample} />
            </div>
            <div className="ms-Grid-col ms-sm6 ms-md8 ms-lg10">
              <Route path={routing.services} component={Main} />
              <Route path={routing.customers} component={Customers} />
              <Route path={routing.reports} component={Reports} />
              <Route path={routing.newCustomer} component={CustomerView} />
              <Route path={routing.debug} render={(localProps) => <Debug {...props} />} />
              <Route path="/" exact component={Home} />
            </div>
          </div>
        </div>
      </Router>
    </ApolloProvider>
  );
};

export const View = connect(mapStateToProps, mapDispatchToProps)(ViewLocal);