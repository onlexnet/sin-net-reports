import { Label, Stack } from "@fluentui/react";
import React from "react";

const stackTokens = { childrenGap: 8 }

interface ReportsViewProps {
  year: number;
  month: number;
}

export const ReportsView: React.FC<ReportsViewProps> = props => {
  const { year, month } = props;
  return (
    <Stack verticalFill>
      <Stack.Item>
        <Stack horizontal tokens={stackTokens}>
          <h1>Raporty</h1>
          <Label>Rok: {year}, miesiąc: {month}</Label>
        </Stack>
      </Stack.Item>
    </Stack>);

};
