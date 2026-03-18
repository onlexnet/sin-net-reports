import { Row, Col } from "antd";
import React, { useState } from "react";
import { Input } from "components/ui/input";

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
          <div className="flex items-center gap-2">
            <label className="text-sm">Rok {props.suffix}</label>
            <Input value={yearText} onChange={onChangeYear} />
          </div>
        </Col>
        <Col>
          <div className="flex items-center gap-2">
            <label className="text-sm">Miesiąc {props.suffix}</label>
            <Input value={monthText} onChange={onChangeMonth} />
          </div>
        </Col>
      </Row>
    );
}
