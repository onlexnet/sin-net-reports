import * as React from "react";
import { MsalAuthProvider } from "react-aad-msal";
import { useState } from "react";

interface Props {
  provider: MsalAuthProvider;
}

export const GetAccessTokenButton = ({ provider }: Props) => {
  const getAuthToken = async () => {
    var token = (await provider.getAccessToken()).accessToken;
    setToken(token);
  };

  const [token, setToken] = useState<string>();

  if (token) {
    return (
    <p>
      { token }
    </p>
  )} else return (
    <div style={{ margin: "40px 0" }}>
      <button onClick={getAuthToken} className="Button">
        Get Access Token
      </button>
    </div>
  );
};
