import { addMonths, addDays } from 'date-fns';

export class TimePeriod {

    private dateFrom: PartsOfDate;
    private dateTo: PartsOfDate;

    constructor(initial: Date, scope: PeriodScope = 'MONTH') {
        this.dateFrom = this.asPartsOfDate(this.adjustDateFrom(initial, scope));
        this.dateTo = this.asPartsOfDate(this.adjustDateTo(initial, scope));
    }

    private adjustDateFrom(current: Date, scope: PeriodScope) {
        const firstDayOfMonth = new Date(current.getFullYear(), current.getMonth(), 1);
        return firstDayOfMonth;
    }

    private adjustDateTo(current: Date, scope: PeriodScope) {
        const firstDayOfMonth = new Date(current.getFullYear(), current.getMonth(), 1);
        return addDays(addMonths(firstDayOfMonth, 1), -1);
    }

    private asPartsOfDate(date: Date): PartsOfDate {
        return {
            year: date.getFullYear(),
            month: date.getMonth() + 1,
            day: date.getDate()
        }
    }
    private asDate(date: PartsOfDate, deltaInDays: number) {
        const current = new Date(date.year, date.month - 1, date.day);
        return addDays(current, deltaInDays);
    }

    getValue() {
        return {
            dateFrom: this.dateFrom,
            dateTo: this.dateTo
        }
    }

    next(): TimePeriod {
        const nextDate = this.asDate(this.dateTo, 1);
        return new TimePeriod(nextDate);
    }

    prev(): TimePeriod {
        const nextDate =  this.asDate(this.dateFrom, -1);
        return new TimePeriod(nextDate);
    }

    toString(): string {
        return this.asDate(this.dateFrom, 0).toLocaleString("default", {
            month: "long",
            year: "numeric",
        })
    }

}

type PeriodScope = 'MONTH';
export interface PartsOfDate {
    year: number;
    /** Month 1-12 */
    month: number;
    /** Day of month 1-31 */
    day: number;
}