import React from "react";

import { HashRouter as Router, Route } from "react-router-dom";
import { NavBasicExample } from "./NavBar";
import { Home } from "./Home";
import { Customers } from "./Customers";
import { Main } from "./services";
import { Reports } from "./reports/Reports";

import ApolloClient from "apollo-boost";
import { routing } from "./Routing";

import { AzureAD, AuthenticationState, IAzureADFunctionProps } from "react-aad-msal";
import { authProvider } from "./authProvider";

import { basicReduxStore } from "./reduxStore";

import GetAccessTokenButton from "./GetAccessTokenButton";

const client = new ApolloClient({
  uri: "http://localhost:8080"
});

export const App: React.FC<{}> = () => {

  const [login, setLogin] = React.useState();

  return (
    <AzureAD provider={authProvider} reduxStore={basicReduxStore}>
      {({ accountInfo, authenticationState, error }: IAzureADFunctionProps) => {

        return (
          <>
          <GetAccessTokenButton provider={authProvider} />
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
                  <Route path="/" exact component={Home} />
                </div>
              </div>
            </div>
          </Router>
          </>
        );
      }}
    </AzureAD>
  );
};
