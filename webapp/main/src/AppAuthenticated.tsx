import React from "react";
import { MsalAuthProvider } from "react-aad-msal";
import { HashRouter as Router, Route } from "react-router-dom";
import { Main } from "./services";
import { Customers } from "./Customers";
import { Reports } from "./reports/Reports";
import { Home } from "./Home";

import { routing } from "./Routing";
import { NavBasicExample } from "./NavBar";
import { GetIdTokenButton } from "./Components/GetIdTokenButton";

interface Props {
  authProvider: MsalAuthProvider;
}

export const View: React.FC<Props> = ({ authProvider }) => {
  return (
    <>
    {/* <GetIdTokenButton provider={authProvider} /> */}
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
};
