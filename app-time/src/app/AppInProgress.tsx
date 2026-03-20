import { InteractionStatus, InteractionType } from "@azure/msal-browser";
import { useMsal, useMsalAuthentication } from "@azure/msal-react";
import React from "react";
import { connect, ConnectedProps } from "react-redux";
import { Dispatch } from "redux";
import { getGraphQlClient } from "../api";
import { InitiateSessionFinishedAction, INITIATE_SESSION_FINISHED } from "../store/session/types";
import { Loader2 } from "lucide-react";
import { setAuthenticatedUser } from "./configuration/ApplicationInsights";
import { TopCenteredContainer } from "components/ui/top-centered-container";

const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    login: (jwtToken: string, email: string) => {
      const action: InitiateSessionFinishedAction = {
        type: INITIATE_SESSION_FINISHED,
        jwtToken: jwtToken,
        email: email
      }
      getGraphQlClient().setHeader("Authorization", `Bearer ${jwtToken}`);
      // Set authenticated user context in Application Insights
      setAuthenticatedUser(email);
      dispatch(action);
    }
  }
}

const mapStateToProps = undefined;

const connector = connect(mapStateToProps, mapDispatchToProps);
type PropsFromRedux = ConnectedProps<typeof connector>;

interface ViewProps extends PropsFromRedux {
}

const request = {
  scopes: ["openid", "profile"],
  loginHint: "undefined",
}

const View: React.FC<ViewProps> = props => {

  const { accounts, inProgress } = useMsal();
  const { login, result } = useMsalAuthentication(InteractionType.Redirect, request);

  if (inProgress === InteractionStatus.None && !result) {
    if (accounts.length > 0) {
      const suggestedAccount = accounts[0];
      request.loginHint = suggestedAccount.username;
      login(InteractionType.Silent, request);
    }
  }

  if (result && inProgress === InteractionStatus.None) {
    props.login(result.idToken, result.account?.username ?? "undefined");
  }

  return (
    <TopCenteredContainer>
      <div className="flex items-center">
        <Loader2 className="h-8 w-8 animate-spin text-muted-foreground" />
        <span className="ml-2 text-muted-foreground">Pracowite sprawdzanie kim jesteś ;) ...</span>
      </div>
    </TopCenteredContainer>
  );
}

export default connector(View)
