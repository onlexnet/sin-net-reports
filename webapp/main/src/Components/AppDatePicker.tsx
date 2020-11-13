import * as React from 'react';
import { Calendar, DayOfWeek, DateRangeType } from 'office-ui-fabric-react/lib/Calendar';
import { DefaultButton } from 'office-ui-fabric-react/lib/Button';
import { addDays, getDateRangeArray } from '@fluentui/date-time-utilities';

export interface ICalendarInlineExampleProps {
  isMonthPickerVisible?: boolean;
  dateRangeType: DateRangeType;
  autoNavigateOnSelection: boolean;
  showGoToToday: boolean;
  showNavigateButtons?: boolean;
  highlightCurrentMonth?: boolean;
  highlightSelectedMonth?: boolean;
  isDayPickerVisible?: boolean;
  showMonthPickerAsOverlay?: boolean;
  showWeekNumbers?: boolean;
  minDate?: Date;
  maxDate?: Date;
  showSixWeeksByDefault?: boolean;
  workWeekDays?: DayOfWeek[];
  firstDayOfWeek?: DayOfWeek;
}

const dayPickerStrings = {
  months: [
    'Styczeń',
    'Luty',
    'Marzec',
    'Kwiecień',
    'Maj',
    'Czerwiec',
    'Lipiec',
    'Sierpień',
    'Wrzesień',
    'Październik',
    'Listopad',
    'Grudzień',
  ],
  shortMonths: ['Sty', 'Lut', 'Mar', 'Kwie', 'Maj', 'Czer', 'Lip', 'Sier', 'Wrze', 'Paź', 'List', 'Gru'],
  days: ['Niedziela', 'Poniedziałek', 'Wtorek', 'Środa', 'Czwartek', 'Piątek', 'Sobota'],
  shortDays: ['N', 'P', 'W', 'Ś', 'C', 'P', 'S'],
  goToToday: 'Idź do dzisiaj',
  weekNumberFormatString: 'Numer tygodnia {0}',
  prevMonthAriaLabel: 'Poprzedni miesiąc',
  nextMonthAriaLabel: 'Następny miesiąc',
  prevYearAriaLabel: 'Poprzedni rok',
  nextYearAriaLabel: 'Następny rok',
  prevYearRangeAriaLabel: 'Previous year range',
  nextYearRangeAriaLabel: 'Next year range',
  closeButtonAriaLabel: 'Zamknij',
  monthPickerHeaderAriaLabel: '{0}, wybierz zmianę roku',
  yearPickerHeaderAriaLabel: '{0}, wybierz zmianę miesiąca',
};
const divStyle: React.CSSProperties = {
  height: 'auto',
};
const buttonStyle: React.CSSProperties = {
  margin: '17px 10px 0 0',
};
let dateRangeString: string | null = null;

export const AppDatePicker: React.FunctionComponent<ICalendarInlineExampleProps> = (
  props: ICalendarInlineExampleProps,
) => {
  const [selectedDateRange, setSelectedDateRange] = React.useState<Date[]>();
  const [selectedDate, setSelectedDate] = React.useState<Date>();

  const onSelectDate = (date: Date, dateRangeArray?: Date[]): void => {
    setSelectedDate(date);
    setSelectedDateRange(dateRangeArray);
  };

  const goPrevious = () => {
    const goPreviousSelectedDate = selectedDate || new Date();
    const dateRangeArray = getDateRangeArray(goPreviousSelectedDate, props.dateRangeType, DayOfWeek.Sunday);
    let subtractFrom = dateRangeArray[0];
    let daysToSubtract = dateRangeArray.length;
    if (props.dateRangeType === DateRangeType.Month) {
      subtractFrom = new Date(subtractFrom.getFullYear(), subtractFrom.getMonth(), 1);
      daysToSubtract = 1;
    }
    const newSelectedDate = addDays(subtractFrom, -daysToSubtract);
    return {
      goPreviousSelectedDate: newSelectedDate,
    };
  };

  const goNext = () => {
    const goNextSelectedDate = selectedDate || new Date();
    const dateRangeArray = getDateRangeArray(goNextSelectedDate, props.dateRangeType, DayOfWeek.Sunday);
    const newSelectedDate = addDays(dateRangeArray.pop()!, 1);

    return {
      goNextSelectedDate: newSelectedDate,
    };
  };

  const onDismiss = () => {
    return selectedDate;
  };

  if (selectedDateRange) {
    const rangeStart = selectedDateRange[0];
    const rangeEnd = selectedDateRange[selectedDateRange.length - 1];
    dateRangeString = rangeStart.toLocaleDateString() + '-' + rangeEnd.toLocaleDateString();
  }

  return (
    <div style={divStyle}>
      {(props.minDate || props.maxDate) && (
        <div>
          Date boundary:
          <span>
            {' '}
            {props.minDate ? props.minDate.toLocaleDateString() : 'Not set'}-
            {props.maxDate ? props.maxDate.toLocaleDateString() : 'Not set'}
          </span>
        </div>
      )}
      <Calendar
        // eslint-disable-next-line react/jsx-no-bind
        onSelectDate={onSelectDate}
        // eslint-disable-next-line react/jsx-no-bind
        onDismiss={onDismiss}
        isMonthPickerVisible={props.isMonthPickerVisible}
        dateRangeType={props.dateRangeType}
        autoNavigateOnSelection={props.autoNavigateOnSelection}
        showGoToToday={props.showGoToToday}
        value={selectedDate!}
        firstDayOfWeek={props.firstDayOfWeek ? props.firstDayOfWeek : DayOfWeek.Sunday}
        strings={dayPickerStrings}
        highlightCurrentMonth={props.highlightCurrentMonth}
        highlightSelectedMonth={props.highlightSelectedMonth}
        isDayPickerVisible={props.isDayPickerVisible}
        showMonthPickerAsOverlay={props.showMonthPickerAsOverlay}
        showWeekNumbers={props.showWeekNumbers}
        minDate={props.minDate}
        maxDate={props.maxDate}
        showSixWeeksByDefault={props.showSixWeeksByDefault}
        workWeekDays={props.workWeekDays}
      />
      {props.showNavigateButtons && (
        <div>
          <DefaultButton
            style={buttonStyle}
            // eslint-disable-next-line react/jsx-no-bind
            onClick={goPrevious}
            text="Previous"
          />
          <DefaultButton
            style={buttonStyle}
            // eslint-disable-next-line react/jsx-no-bind
            onClick={goNext}
            text="Next"
          />
        </div>
      )}
    </div>
  );
};