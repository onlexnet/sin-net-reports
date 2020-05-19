import * as React from "react";
import { MsalAuthProvider } from "react-aad-msal";
import { useState } from "react";
import { IdToken } from "msal/lib-commonjs/IdToken";

interface Props {
  provider: MsalAuthProvider;
}

export const GetIdTokenButton = ({ provider }: Props) => {
  const getAuthToken = async () => {
    var token = (await provider.getIdToken()).idToken;
    setToken(token);
  };

  const [token, setToken] = useState<IdToken>();

  if (token) {
    return (
    <p>
      { token.rawIdToken }
    </p>
  )} else return (
    <div style={{ margin: "40px 0" }}>
      <p>
        It's also possible to renew the IdToken. If a valid token is in the
        cache, it will be returned. Otherwise a renewed token will be requested.
        If the request fails, the user will be forced to login again.
      </p>
      <button onClick={getAuthToken} className="Button">
        Get IdToken
      </button>
    </div>
  );
};
