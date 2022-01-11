import { Label, Link, Stack, TextField } from "@fluentui/react";
import React, { useCallback, useState } from "react";
import { isNumericLiteral } from "typescript";
import { addressProvider } from "../addressProvider";
import { LocalDate } from "../store/viewcontext/TimePeriod";

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

  const [fromYearText, setFromYearText] = useState<string | undefined>(from.year.toString());
  const [fromMonthText, setFromMonthText] = useState<string | undefined>(from.month.toString());
  const [fromYear, setFromYear] = useState<number | null>(from.year);
  const [FromMonth, setFromMonth] = useState<number | null>(from.month);

  const yearRe = /^(19|20)\d{2}$/;
  const monthRe = /^(0?[1-9]|1[012])$/;

  const onChangeYear = (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
    setFromYearText(newValue);

    const newValueAsString = newValue ?? "";
    const newValueAsInt = yearRe.test(newValueAsString) ? parseInt(newValueAsString) : null;
    setFromYear(newValueAsInt);
  }

  const onChangeMonth = (event: React.FormEvent<HTMLInputElement | HTMLTextAreaElement>, newValue?: string) => {
    setFromMonthText(newValue);

    const newValueAsString = newValue ?? "";
    const newValueAsInt = monthRe.test(newValueAsString) ? parseInt(newValueAsString) : null;
    setFromMonth(newValueAsInt);
  }

  return (
    <Stack verticalFill style={{ padding: 10 }}>
      <Stack.Item>
        <Stack tokens={stackTokens}>
          <h1>Raporty</h1>
          <Stack horizontal>
            <Stack.Item>
              <TextField label="Rok" value={fromYearText} onChange={onChangeYear} />
            </Stack.Item>
            <Stack.Item>
              <TextField label="Miesiąc" value={fromMonthText} onChange={onChangeMonth} />
            </Stack.Item>
          </Stack>
          <Link
            disabled={fromYear == null || FromMonth == null}
            onClick={() => { openInNewTab(addressProvider().host + `/api/raporty/klienci/${projectId}/${fromYearText}/${fromMonthText}`); }}
          >
            Raport miesięczny
          </Link>


          <Link onClick={() => {
            openInNewTab(addressProvider().host + `/api/raporty/2/${projectId}/${fromYearText}/${fromMonthText}`);
          }}>
            Zestawienie sumaryczne
          </Link>

        </Stack>
      </Stack.Item>
    </Stack>);

};

