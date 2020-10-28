import React from "react";
import { PrimaryButton } from "office-ui-fabric-react";

export const View: React.FC<{ login: () => void }> = ({ login }) => {
  return (
    <PrimaryButton onClick={login}>Zaloguj się do aplikacji XD ...</PrimaryButton>
  );
};
