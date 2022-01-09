import { Label, Link, Stack } from "@fluentui/react";
import React from "react";
import { addressProvider } from "../addressProvider";
import { LocalDate } from "../store/viewcontext/TimePeriod";

const stackTokens = { childrenGap: 8 }

interface ReportsViewProps {
  projectId: string;
  from: LocalDate;
}


export const ReportsView: React.FC<ReportsViewProps> = props => {
  const { projectId, from } = props;
  const { year, month } = from;
  const openInNewTab = (url: string) => {
    window.open(url, '_blank');
  }
  return (
    <Stack verticalFill style={{ padding: 10 }}>
      <Stack.Item>
        <Stack tokens={stackTokens}>
          <h1>Raporty</h1>
          <Label>Rok: {year}, miesiąc: {month}</Label>
          <Link onClick={() => {
            openInNewTab(addressProvider().host + `/api/raporty/klienci/${projectId}/${year}/${month}`);
          }}>
            Raport miesięczny
          </Link>


          <Link onClick={() => {
            openInNewTab(addressProvider().host + `/api/raporty/2/${projectId}/${year}/${month}`);
          }}>
            Zestawienie sumaryczne
          </Link>

        </Stack>
      </Stack.Item>
    </Stack>);

};

