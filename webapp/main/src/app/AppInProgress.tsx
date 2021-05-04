import { InteractionStatus, InteractionType, RedirectRequest, SsoSilentRequest } from "@azure/msal-browser";
import { useMsal, useMsalAuthentication } from "@azure/msal-react";
import { IStackTokens, Label, Spinner, Stack } from "@fluentui/react";
import React from "react";
import { connect, ConnectedProps } from "react-redux";
import { Dispatch } from "redux";
import { graphQlClient } from "../api";
import { InitiateSessionFinishedAction, INITIATE_SESSION_FINISHED } from "../store/session/types";

const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    login: (jwtToken: string, email: string) => {
      const action: InitiateSessionFinishedAction = {
        type: INITIATE_SESSION_FINISHED,
        jwtToken: jwtToken,
        email: email
      }
      graphQlClient.setHeader("Authorization", `Bearer ${jwtToken}`);
      dispatch(action);
    }
  }
}

const mapStateToProps = undefined;

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface ViewProps extends PropsFromRedux {
}

const request: RedirectRequest | SsoSilentRequest = {
  scopes: ["openid", "profile"],
}

const View: React.FC<ViewProps> = props => {

  const { accounts, inProgress } = useMsal();
  const { login, result } = useMsalAuthentication(InteractionType.Redirect, request);

  if (inProgress === InteractionStatus.None && !result) {
    if (accounts.length > 0) {
      const suggestedAccount = accounts[0];
      request.loginHint = suggestedAccount.username;
      login(InteractionType.Silent, request);
    } else {
      login(InteractionType.Redirect, request);
    }
  }

  if (result && inProgress === InteractionStatus.None) {
    props.login(result.idToken, result.account?.username ?? "undefined");
  }

  const stackTokens: IStackTokens = {
    childrenGap: 20,
    maxWidth: 250,
  };

  return (
    <Stack >
      <Stack.Item align="center">
        <span>
          <Spinner label="Sprawdzanie konta użytkownika ..." ariaLive="assertive" labelPosition="right" />
        </span>
      </Stack.Item>
    </Stack>
  );
}

export default connector(View)
