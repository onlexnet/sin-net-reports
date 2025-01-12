import { DatePicker } from 'antd';
import { LocalDate } from '../store/viewcontext/TimePeriod';
import React from 'react';
import dayjs from 'dayjs';

export interface AppDatePickerProps {
  onSelectDate(value: LocalDate): void;
  gotoTodayText: string;
  current: LocalDate;
}

const divStyle: React.CSSProperties = {
  height: 'auto',
};

export const AppDatePicker: React.FC<AppDatePickerProps> = props => {

  const { current: currentDate } = props;

  const onSelectDate = (date: dayjs.Dayjs | null) => {
    if (date) {
      const asLocalDate = LocalDate.of(date.toDate());
      props.onSelectDate(asLocalDate);
    }
  };

  return (
    <div style={divStyle}>
      <DatePicker
        onChange={onSelectDate}
        defaultValue={dayjs(LocalDate.toDate(currentDate))} />
    </div>
  );
};
