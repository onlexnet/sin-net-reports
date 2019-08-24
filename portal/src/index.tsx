import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter as Router } from "react-router-dom";
import "./index.css";
import App from "./pages/App";
import * as serviceWorker from "./serviceWorker";
import { ApolloProvider } from "react-apollo";
import ApolloClient from "apollo-boost";
import { IClientData as IClientState } from "./components/Shared/ClientData";

const defaultClientState: IClientState = {
  isLoggedIn: !!localStorage.getItem("authToken")
};
const client = new ApolloClient({
  uri: "http://localhost:8000/graphql/",
  clientState: {
    defaults: defaultClientState
  },
  fetchOptions: {
    // ApolloClient adds to every operation Authorization header
    // TODO migrate to https://www.apollographql.com/docs/react/advanced/boost-migration/
    credentials: "include"
  },
  request: operation => {
    const token = localStorage.getItem("authToken") || "";
    operation.setContext({
      headers: {
        Authorization: `JWT ${token}`
      }
    });
  }
});

ReactDOM.render(
  <ApolloProvider client={client}>
    <Router>
      <App />
    </Router>
  </ApolloProvider>,
  document.getElementById("root")
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
