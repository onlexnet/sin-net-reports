import React from "react";
import { Stack, Text, Link, FontWeights } from "office-ui-fabric-react";

import { HashRouter as Router, Route } from "react-router-dom";
import { NavBasicExample } from "./NavBar";
import { Home } from "./Home";
import { Services } from "./Services";
import { NotFound } from "./NotFound";
import { Customers } from "./Customers";

export const App: React.FunctionComponent = () => {
  return (
    <Router>
      <div className="ms-Grid" dir="ltr">
        <div className="ms-Grid-row">
          <div className="ms-Grid-col ms-sm6 ms-md4 ms-lg2">
            <Route path="/" component={NavBasicExample} />
          </div>
          <div className="ms-Grid-col ms-sm6 ms-md8 ms-lg10">
            <Route path="/services" component={Services} />
            <Route path="/customers" component={Customers} />
            <Route path="/" component={Home} />
          </div>
        </div>
      </div>
    </Router>
  );
};
