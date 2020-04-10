import React, { useState } from "react";
import { Content } from "./Content";
import { ServiceCommandBar } from "./Commands";
import { IconButton } from "office-ui-fabric-react";

export const Main: React.FC<{}> = (props) => {
  const [period, setPeriod] = useState(new Date());

  /**
   * @param delta allows to change current periond (year with month).
   *              Use only values +1 / -1 to set next month / previous month.
   */
  const changePeriod = (delta: number) => {
    const newPeriod = new Date(period.getFullYear(), period.getMonth() + delta);
    setPeriod(newPeriod);
  };

  return (
    <>
      <ServiceCommandBar
        onNextMonthRequested={() => changePeriod(+1)}
        onPreviousMonthRequested={() => changePeriod(-1)}
      />

      {period.toLocaleString("default", {
        month: "long",
        year: "numeric",
      })}

      <Content />
    </>
  );
};
