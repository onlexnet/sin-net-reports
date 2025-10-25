import { InteractionStatus, InteractionType } from "@azure/msal-browser";
import { useMsal, useMsalAuthentication } from "@azure/msal-react";
import React from "react";
import { connect, ConnectedProps } from "react-redux";
import { Dispatch } from "redux";
import { graphQlClient } from "../api";
import { InitiateSessionFinishedAction, INITIATE_SESSION_FINISHED } from "../store/session/types";
import { Col, Row, Spin } from "antd";
import { setAuthenticatedUser } from "./configuration/ApplicationInsights";

const mapDispatchToProps = (dispatch: Dispatch) => {
  return {
    login: (jwtToken: string, email: string) => {
      const action: InitiateSessionFinishedAction = {
        type: INITIATE_SESSION_FINISHED,
        jwtToken: jwtToken,
        email: email
      }
      graphQlClient.setHeader("Authorization", `Bearer ${jwtToken}`);
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
    <Row justify="center" align="middle" style={{ height: '100vh' }}>
      <Col>
        <Spin tip="Pracowite sprawdzanie kim jesteś ;) ..." />
      </Col>
    </Row>
  );
}

export default connector(View)
