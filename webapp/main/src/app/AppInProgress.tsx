import { InteractionStatus, InteractionType, RedirectRequest, SsoSilentRequest } from "@azure/msal-browser";
import { useMsal, useMsalAuthentication } from "@azure/msal-react";
import React, { useEffect } from "react";
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


const View: React.FC<ViewProps> = props => {

  let request : RedirectRequest | SsoSilentRequest = {
    scopes: ["openid", "profile"],
  }
  let interactionType = InteractionType.Redirect;

  const { instance, accounts, inProgress } = useMsal();
  const { login, result, error } = useMsalAuthentication(interactionType, request);
  
  useEffect(() => {
    if (error) {
      console.error(error);
      login(InteractionType.Redirect, request);
    }
  }, [error]);

  useEffect(() => {
    alert(inProgress)
    if (inProgress === InteractionStatus.None && accounts.length > 0) {
      const suggestedAccount = accounts[0];
      request.loginHint = suggestedAccount.username;
      login(InteractionType.Silent, request);
    }

    if (inProgress === InteractionStatus.None && !accounts.length) {
      login(InteractionType.Redirect, request);
    }

    if (result) {
      props.login(result!.idToken, result!.account?.username ?? "undefined");
    }

  }, [inProgress])

  return (
    <>
      <p>Login in progress...</p>
    </>
  );
}

export default connector(View)
