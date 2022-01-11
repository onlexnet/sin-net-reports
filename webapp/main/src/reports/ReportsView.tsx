import { Link, Stack } from "@fluentui/react";
import React, { useState } from "react";
import { addressProvider } from "../addressProvider";
import { LocalDate } from "../store/viewcontext/TimePeriod";
import { PeriodSelector } from "./PeriodSelector";

const stackTokens = { childrenGap: 8 }

interface ReportsViewProps {
  projectId: string;
  from: LocalDate;
}


export const ReportsView: React.FC<ReportsViewProps> = props => {
  const { projectId, from } = props;
  const openInNewTab = (url: string) => {
    window.open(url, '_blank');
  }

  const [fromYear, setFromYear] = useState<number | undefined>(from.year);
  const [fromMonth, setFromMonth] = useState<number | undefined>(from.month);

  const [toYear, setToYear] = useState<number | undefined>(from.year);
  const [toMonth, setToMonth] = useState<number | undefined>(from.month);

  return (
    <Stack verticalFill style={{ padding: 10 }}>
      <Stack.Item>
        <Stack tokens={stackTokens}>
          <h1>Raporty</h1>
          <PeriodSelector suffix="OD:" year={fromYear} month={fromMonth} onYearChanged={setFromYear} onMonthChanged={setFromMonth}/>
          <PeriodSelector suffix="DO:" year={toYear} month={toMonth} onYearChanged={setToYear} onMonthChanged={setToMonth}/>

          <Link
            disabled={fromYear == null || fromMonth == null}
            onClick={() => { openInNewTab(addressProvider().host + `/api/raporty/klienci/${projectId}/${fromYear}/${fromMonth}`); }}
          >
            Raport miesięczny (na datę OD)
          </Link>


          <Link onClick={() => {
            openInNewTab(addressProvider().host + `/api/raporty/2/${projectId}?yearFrom=${fromYear}&monthFrom=${fromMonth}&yearTo=${toYear}&monthTo=${toMonth}`);
          }}>
            Zestawienie sumaryczne (na okres OD - DO)
          </Link>

        </Stack>
      </Stack.Item>
    </Stack>);

};

