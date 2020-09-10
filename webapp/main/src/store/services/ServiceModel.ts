import { LocalDate } from "../viewcontext/TimePeriod";

export interface ServiceAppModel {
    servicemanName: string,
    description: string,
    customerName: string,
    when: LocalDate
}