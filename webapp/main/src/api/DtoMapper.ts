import { LocalDate } from "../store/viewcontext/TimePeriod";

export const toModel = (dtoDate: string): LocalDate => {
    const yearAsString = dtoDate.substring(0, 4);
    const monthAsString = dtoDate.substring(5, 7);
    const dayAsString = dtoDate.substring(8, 10);
    const year = Number(yearAsString);
    const month = Number(monthAsString);
    const day = Number(dayAsString);

    return { year, month, day };
}

export const dates = (it: LocalDate | null | undefined) => {
    if (!it) {
        const now = new Date();
        it = {
            year: now.getFullYear(),
            month: now.getMonth() + 1,
            day: now.getDay()
        }
    }
    return {
        toRawValue: rawValue(it)
    };
}

const rawValue = (it: LocalDate) => {
    return ('0000' + it.year).substr(-4) +
        ('00' + it.month).substr(-2) +
        ('00' + it.day).substr(-2);
}

