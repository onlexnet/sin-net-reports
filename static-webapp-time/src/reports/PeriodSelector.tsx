import { Input, Row, Col } from "antd";
import React, { useState } from "react";

interface PeriodSelectorProps {
    suffix: string,
    year?: number,
    month?: number,
    onYearChanged: (value?: number) => void,
    onMonthChanged: (value?: number) => void,
}

export const PeriodSelector: React.FC<PeriodSelectorProps> = props => {

    const yearRe = /^(19|20)\d{2}$/;
    const monthRe = /^(0?[1-9]|1[012])$/;
  
    const onChangeYear = (e: React.ChangeEvent<HTMLInputElement>) => {
      const newValue = e.target.value;
      setYearText(newValue);
  
      const newValueAsInt = yearRe.test(newValue) ? parseInt(newValue) : undefined;
      if (newValueAsInt === props.year) return;
      props.onYearChanged(newValueAsInt);
    }
  
    const onChangeMonth = (e: React.ChangeEvent<HTMLInputElement>) => {
      const newValue = e.target.value;
      setMonthText(newValue);
  
      const newValueAsInt = monthRe.test(newValue) ? parseInt(newValue) : undefined;
      if (newValueAsInt === props.month) return;
      props.onMonthChanged(newValueAsInt);
    }
  
    const [yearText, setYearText] = useState<string | undefined>((props.year ?? '').toString());
    const [monthText, setMonthText] = useState<string | undefined>((props.month ?? '').toString());
  
    return (
      <Row gutter={16}>
        <Col>
          <Input addonBefore={`Rok ${props.suffix}`} value={yearText} onChange={onChangeYear} />
        </Col>
        <Col>
          <Input addonBefore={`Miesiąc ${props.suffix}`} value={monthText} onChange={onChangeMonth} />
        </Col>
      </Row>
    );
}
