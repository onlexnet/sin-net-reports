import React, { useState } from "react";
import { Register } from "./Register";
import { RouteComponentProps } from "react-router";
import { Login } from "./Login";

type AuthDefaultView = "SIGN_IN" | "SIGN_OUT";

const Index: React.FC<RouteComponentProps<{}>> = props => {
  const [view, setView] = useState<AuthDefaultView>("SIGN_OUT");

  return (
    <>
      {view === "SIGN_OUT" && (
        <Register navigateToSignIn={() => setView("SIGN_IN")} />
      )}
      {view === "SIGN_IN" && <Login />}
    </>
  );
};

export default Index;
