import { TimePeriod, LocalDate } from "../store/viewcontext/TimePeriod"

export const asDtoDates = (entry: TimePeriod) => {
    const current = entry.getValue();
    const dtoDate = (it : LocalDate) => {
        return ('0000' + it.year).substr(-4) + "/" +
               ('00' + it.month).substr(-2) + "/" +
               ('00' + it.day).substr(-2);
    }
    return {
        dateFrom: dtoDate(current.dateFrom),
        dateTo: dtoDate(current.dateTo)
    }
}