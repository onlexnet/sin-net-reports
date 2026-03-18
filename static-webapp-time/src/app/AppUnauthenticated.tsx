import React from "react";
import { Button } from "components/ui/button";
import { getDisplayVersion } from './configuration/BuildInfo';

export const View: React.FC<{ login: () => void }> = ({ login }) => {
  return (
    <div className="flex flex-col items-center gap-3">
      <Button onClick={login}>Zaloguj się do aplikacji ...</Button>
      <span className="text-sm text-muted-foreground">{getDisplayVersion()}</span>
    </div>
  );
};
