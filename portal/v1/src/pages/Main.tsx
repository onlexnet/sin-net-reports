import React from "react";
import { Switch, Route, RouteComponentProps } from "react-router";
import { Link } from "react-router-dom";
import { Customer } from "./Customer";
import { Customers } from "./Customers";

export const Main: React.FC<RouteComponentProps<{}>> = props => {
  const { path, url } = props.match;
  return (
    <div>
      <ul>
        <li>
          <Link to={`${url}/customers`}>Users</Link>
        </li>
        <li>
          <Link to={`${url}/customers/1`}>User 1</Link>
        </li>
      </ul>
      <Switch>
        <Route exact path="/main/customers" component={Customers} />
        <Route
          path={`${path}/customers/:id`}
          render={props => (
            <Customer
              address="an address"
              city="a city"
              name="a name"
              {...props}
            />
          )}
        />
      </Switch>
    </div>
  );
};

export default Main;
