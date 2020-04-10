import React, { useState } from "react";
import { Content } from "./Content";
import { CommandBarBasicExample } from "./Commands";
import { IconButton } from "office-ui-fabric-react";

export const Main: React.FC<{}> = props => {
  const [period, setPeriod] = useState(new Date());

  return (
    <>
      <CommandBarBasicExample />
      <IconButton
        iconProps={{ iconName: "DoubleChevronLeft" }}
        title="Poprzedni miesiąc"
        ariaLabel="<<"
        onClick={() => {
          const newPeriod = new Date(
            period.getFullYear(),
            period.getMonth() - 1
          );
          setPeriod(newPeriod);
        }}
      />
      <IconButton
        iconProps={{ iconName: "DoubleChevronRight" }}
        title="Następny miesiąc"
        ariaLabel=">>"
        onClick={() => {
          const newPeriod = new Date(
            period.getFullYear(),
            period.getMonth() + 1
          );
          setPeriod(newPeriod);
        }}
      />

      {period.toLocaleString("default", {
        month: "long",
        year: "numeric"
      })}
      <Content />
    </>
  );
};
