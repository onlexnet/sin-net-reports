import React from "react";

import { HashRouter as Router, Route } from "react-router-dom";
import { NavBasicExample } from "./NavBar";
import { Home } from "./Home";
import { Customers } from "./Customers";
import { Main } from "./services";
import { Reports } from "./reports/Reports";

import ApolloClient from 'apollo-boost';
import { routing } from "./Routing";

const client = new ApolloClient({
  uri: 'http://localhost:8080',
});

export const App: React.FC<{}> = () => {
  return (
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
  );
};
