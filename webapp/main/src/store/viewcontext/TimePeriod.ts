export class TimePeriod {

    private dateFrom: PartsOfDate;
    private dateTo: PartsOfDate;

    constructor(initial: Date, scope: PeriodScope = 'MONTH') {
        this.dateFrom = this.asParts(this.adjustDateFrom(initial, scope));
        this.dateTo = this.asParts(this.adjustDateTo(initial, scope));
    }

    private adjustDateFrom(current: Date, scope: PeriodScope) {
        return new Date(current.getFullYear(), current.getMonth(), 1);
    }

    private adjustDateTo(current: Date, scope: PeriodScope) {
        return new Date(current.getFullYear(), current.getMonth() + 1, 1);
    }

    private asParts(date: Date): PartsOfDate {
        return {
            year: date.getFullYear(),
            month: date.getMonth(),
            day: 1
        }
    }
    private asDate(date: PartsOfDate, deltaInDays: number): Date {
        return new Date(date.year, date.month, date.day);
    }

    next(): TimePeriod {
        const nextDate = this.asDate(this.dateTo, +1);
        return new TimePeriod(nextDate);
    }

    prev(): TimePeriod {
        const nextDate = this.asDate(this.dateFrom, -1);
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
interface PartsOfDate {
    year: number;
    month: number;
    day: number;
}