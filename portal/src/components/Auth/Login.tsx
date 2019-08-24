import React, { useState, SyntheticEvent } from "react";
import gql from "graphql-tag";
import { Mutation, MutationFn } from "react-apollo";
import { Label, Card, Button } from "@blueprintjs/core";
import { ApolloClient } from "apollo-boost";
import { IClientData } from "../Shared/ClientData";

const LOGIN_MUTATION = gql`
  mutation($username: String!, $password: String!) {
    tokenAuth(username: $username, password: $password) {
      token
    }
  }
`;

interface IData {
  tokenAuth: {
    token: string;
  };
}

interface IParams {
  username: string;
  password: string;
}

export const Login: React.FC = props => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const onSubmit = async (
    e: SyntheticEvent,
    mutation: MutationFn<IData, IParams>,
    client: ApolloClient<{}>
  ) => {
    e.preventDefault();

    const response = await mutation();
    if (response) {
      const token = response.data!.tokenAuth.token;
      localStorage.setItem("authToken", token);
      client.writeData<IClientData>({
        data: { isLoggedIn: true }
      });
    }
  };

  return (
    <Mutation<IData, IParams>
      mutation={LOGIN_MUTATION}
      variables={{ username, password }}
    >
      {(mutation, { client }) => {
        return (
          <Card>
            <Label>
              User name
              <input onChange={e => setUsername(e.target.value)} />
            </Label>
            <Label>
              Password
              <input
                type="password"
                onChange={e => setPassword(e.target.value)}
              />
            </Label>
            <Button
              type="submit"
              onClick={(e: SyntheticEvent) => onSubmit(e, mutation, client)}
            >
              Sing in ...
            </Button>
          </Card>
        );
      }}
    </Mutation>
  );
};
