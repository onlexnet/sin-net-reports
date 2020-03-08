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
        <Route path="/" component={NavBasicExample} />
        <Route path="/services" component={Services} />
        <Route path="/customers" component={Customers} />
        <Route path="/" component={Home} />
      </Router>
  );
};
