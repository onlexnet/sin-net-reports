import { Typography, Layout, Button, Space } from "antd";
import React, { useState } from "react";
import { addressProvider } from "../addressProvider";
import { LocalDate } from "../store/viewcontext/TimePeriod";
import { PeriodSelector } from "./PeriodSelector";

const { Title } = Typography;
const { Content } = Layout;

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
    <Layout style={{ padding: 10 }}>
      <Content>
        <Title level={1}>Raporty</Title>

        <Space direction="vertical">
          <PeriodSelector suffix="OD:" year={fromYear} month={fromMonth} onYearChanged={setFromYear} onMonthChanged={setFromMonth} />
          <PeriodSelector suffix="DO:" year={toYear} month={toMonth} onYearChanged={setToYear} onMonthChanged={setToMonth} />

          <Button
            disabled={fromYear == null || fromMonth == null}
            type="link"
            onClick={() => { openInNewTab(addressProvider().host + `/api/raporty/klienci/${projectId}/${fromYear}/${fromMonth}`); }}
          >
            Raport miesięczny - załączniki do faktur
          </Button>

          <Button
            type="link"
            onClick={() => {
            openInNewTab(addressProvider().host + `/api/raporty/2/${projectId}?yearFrom=${fromYear}&monthFrom=${fromMonth}&yearTo=${toYear}&monthTo=${toMonth}`);
          }}>
            Zestawienie sumaryczne godzin
          </Button>

          <Button
            type="link"
            onClick={() => {
            openInNewTab(addressProvider().host + `/api/raporty/3/${projectId}`);
          }}>
            Lista klientów przypisanych do operatorów
          </Button>
        </Space>
      </Content>
    </Layout>
  );
};

