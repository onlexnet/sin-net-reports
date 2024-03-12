import { InteractionStatus, InteractionType, RedirectRequest, SsoSilentRequest } from "@azure/msal-browser";
import { useMsal, useMsalAuthentication } from "@azure/msal-react";
import { Spinner, Stack } from "@fluentui/react";
import React, { useReducer } from "react";
import { graphQlClient } from "../api";
import { InitiateSessionFinishedAction, INITIATE_SESSION_FINISHED } from "../store/session/types";
import { initialState, reducer } from "../store/reducers";

interface ViewProps {
}

const request: RedirectRequest | SsoSilentRequest = {
  scopes: ["openid", "profile"],
}

const View: React.FC<ViewProps> = props => {
  const [state, dispatch] = useReducer(reducer, initialState);
  const { accounts, inProgress } = useMsal();
  const { login, result } = useMsalAuthentication(InteractionType.Redirect, request);

  alert(JSON.stringify(state))
  const login2 = (jwtToken: string, email: string) => {
    const action: InitiateSessionFinishedAction = {
      type: INITIATE_SESSION_FINISHED,
      jwtToken: jwtToken,
      email: email
    }
    graphQlClient.setHeader("Authorization", `Bearer ${jwtToken}`);
    dispatch(action);
  }

  if (inProgress === InteractionStatus.None && !result) {
    alert(3)
    if (accounts.length > 0) {
      alert(4)
      const suggestedAccount = accounts[0];
      request.loginHint = suggestedAccount.username;
      login(InteractionType.Silent, request);
    }
  }

  alert(5)
  if (result && inProgress === InteractionStatus.None) {
    alert(6)
    login2(result.idToken, result.account?.username ?? "undefined");
  }

  return (
    <Stack >
      <Stack.Item align="center">
        <span>
          <Spinner label="Pracowite sprawdzanie kim jesteś ;) ..." ariaLive="assertive" labelPosition="right" />
        </span>
      </Stack.Item>
    </Stack>
  );
}

export default View
