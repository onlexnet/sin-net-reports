import React from "react";
import { Button } from "office-ui-fabric-react";

export const View: React.FC<{ login: () => void }> = ({ login }) => {
  return (
    <Button onClick={login}>Zaloguj się do aplikacji ...</Button>
  );
};
