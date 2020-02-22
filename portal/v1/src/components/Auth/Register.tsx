import React, { useState, SyntheticEvent } from "react";
import { Label, Classes, Button, FormGroup, Card } from "@blueprintjs/core";
import { Mutation, MutationFn } from "react-apollo";
import gql from "graphql-tag";
import { Link } from "react-router-dom";

const REGISTER_MUTATION = gql`
  mutation($username: String!, $password: String) {
    createUser(userName: $username, password: $password) {
      user {
        id
      }
    }
  }
`;

interface IData {}

interface IVariables {
  username: string;
  password: string;
}

enum REGISTRATION_STATE {
  NOT_STARTED,
  PENDING,
  COMPLETED
}

interface IRegisterProps {
  navigateToSignIn: () => void;
}

export const Register: React.FC<IRegisterProps> = ({ navigateToSignIn }) => {
  const [state, setPending] = useState(REGISTRATION_STATE.NOT_STARTED);

  const onSubmitFn = (mutation: MutationFn<IData, IVariables>) => {
    return (username: string, password: string) => {
      mutation({
        variables: {
          username,
          password
        }
      });
    };
  };

  return (
    <Mutation<IData, IVariables>
      mutation={REGISTER_MUTATION}
      onCompleted={d => {
        setPending(REGISTRATION_STATE.COMPLETED);
        navigateToSignIn();
      }}
    >
      {(createUserMutation, { loading, error }): JSX.Element => {
        return (
          <>
            {state === REGISTRATION_STATE.NOT_STARTED && (
              <RegistrationForm onSubmit={onSubmitFn(createUserMutation)} />
            )}
            {state === REGISTRATION_STATE.COMPLETED && (
              <RegistrationCompleted />
            )}
            <Link to="#" onClick={navigateToSignIn} >Sign in</Link>
          </>
        );
      }}
    </Mutation>
  );
};

interface IRegisterForm {
  onSubmit: (userName: string, value: string) => void;
}
const RegistrationForm: React.FC<IRegisterForm> = ({ onSubmit }) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const onChange = (e: SyntheticEvent) => {
    e.preventDefault();
    onSubmit(username, password);
  };

  return (
    <FormGroup>
      <Label>
        User name:
        <input
          className={Classes.INPUT}
          onChange={e => setUsername(e.target.value)}
        />
      </Label>
      <Label>
        Password
        <input
          type="password"
          className={Classes.INPUT}
          onChange={e => setPassword(e.target.value)}
        />
      </Label>
      <Button type="submit" onClick={onChange}>
        Create account
      </Button>
    </FormGroup>
  );
};

const RegistrationPending: React.FC = props => {
  return (
    <Card>
      <p>............................</p>
    </Card>
  );
};

const RegistrationCompleted: React.FC = props => {
  return (
    <Card>
      <p>Registraction complete</p>
    </Card>
  );
};
