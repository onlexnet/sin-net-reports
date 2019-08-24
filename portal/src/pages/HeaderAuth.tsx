import React from "react";
import { Button } from "@blueprintjs/core";
import { IconNames } from "@blueprintjs/icons";
import { Query } from "react-apollo";

export const HeaderAuth: React.FC = (props: any) => {
  return (
    <Button className="bp3-minimal" icon={IconNames.LOG_OUT} text="Sign out" />
  );
};
