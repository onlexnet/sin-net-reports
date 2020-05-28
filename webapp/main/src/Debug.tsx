import { GetIdTokenButton } from "./Components/GetIdTokenButton";
import { GetAccessTokenButton } from "./Components/GetAccessToken";
import { MsalAuthProvider, IAccountInfo } from "react-aad-msal";
import React from "react";

interface Props {
  authProvider: MsalAuthProvider;
  accountInfo: IAccountInfo;
}

export const Debug: React.FC<Props> = ({ authProvider} ) => {
  return (
    <>
      <GetIdTokenButton provider={authProvider} />
      <GetAccessTokenButton provider={authProvider} />
    </>
  );
};
