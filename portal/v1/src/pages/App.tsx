import React from "react";
import "./App.css";
import { Header } from "./Header";
import { Main } from "./Main";
import EmHomety from "./Home";
import { Route, Switch } from "react-router";
import { NotFound } from "./NotFound";
import { Link } from "react-router-dom";
import Auth from '../components/Auth'

const App: React.FC = () => {
  return (
    <div className="App">
      <Header />

      <ul>
        <li>
          <Link to={`/auth`}>Login</Link>
        </li>
        <li>
          <Link to={`/main`}>Main</Link>
        </li>
      </ul>

      <Switch>
        <Route exact path="/" component={EmHomety} />
        <Route path="/auth" component={Auth} />
        <Route path="/main" component={Main} />
        <Route component={NotFound} />
      </Switch>
    </div>
  );
};

export default App;
