import React from "react";
import { authModule } from "../msal/autorun";

export const View: React.FC<{}> = props => {
  authModule.login();

  return <div>Login in progress ...</div>;
};
