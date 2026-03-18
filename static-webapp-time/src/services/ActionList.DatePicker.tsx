import React, { useState } from 'react';
import { format } from 'date-fns';
import { pl } from 'date-fns/locale';
import { CalendarIcon } from 'lucide-react';
import { LocalDate } from '../store/viewcontext/TimePeriod';
import { Calendar } from 'components/ui/calendar';
import { Popover, PopoverContent, PopoverTrigger } from 'components/ui/popover';
import { Button } from 'components/ui/button';
import { cn } from 'lib/utils';

export interface AppDatePickerProps {
  onSelectDate(value: LocalDate): void;
  gotoTodayText: string;
  current: LocalDate;
}

export const AppDatePicker: React.FC<AppDatePickerProps> = props => {
  const { current: currentDate } = props;
  const [open, setOpen] = useState(false);

  const selectedDate = LocalDate.toDate(currentDate) ?? undefined;

  const onDaySelect = (date: Date | undefined) => {
    if (date) {
      props.onSelectDate(LocalDate.of(date));
      setOpen(false);
    }
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          className={cn('w-full justify-start text-left font-normal', !selectedDate && 'text-muted-foreground')}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          {selectedDate ? format(selectedDate, 'dd.MM.yyyy', { locale: pl }) : <span>Wybierz datę</span>}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0">
        <Calendar
          mode="single"
          selected={selectedDate}
          onSelect={onDaySelect}
          defaultMonth={selectedDate}
          locale={pl}
          initialFocus
        />
      </PopoverContent>
    </Popover>
  );
};
