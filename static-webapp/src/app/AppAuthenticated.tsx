import React, { useReducer } from "react";

import { ApolloProvider } from "@apollo/react-hooks";
import { apolloClientFactory } from "../api";
import { MainView } from "./mainview/MainView";
import { initialState, reducer } from "../store/reducers";

interface Props {
}

const ViewLocal: React.FC<Props> = (props) => {
  const [state, dispatch] = useReducer(reducer, initialState);

  const client = apolloClientFactory(state.auth.idToken);

  return (
    <ApolloProvider client={client}>
      <MainView />
    </ApolloProvider>
  );
};

export const View = ViewLocal;