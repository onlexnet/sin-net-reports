export class LocalDate {

    private year: number;
    private month: number;
    private day: number;

    constructor(initial: Date) {
        this.year = initial.getFullYear();
        this.month = initial.getMonth();
        this.day = 1;
    }


    next(): LocalDate {
        const nextDate = new Date(this.year, this.month + 1, this.day);
        return new LocalDate(nextDate);
    }

    prev(): LocalDate {
        const nextDate = new Date(this.year, this.month + -1, this.day);
        return new LocalDate(nextDate);
    }

    toString(): string {
        return new Date(this.year, this.month, this.day).toLocaleString("default", {
            month: "long",
            year: "numeric",
        })
    }

}