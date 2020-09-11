import { LocalDate } from "../viewcontext/TimePeriod";

export interface ServiceAppModel {
    servicemanName: string,
    description: string,
    customerName: string,
    when: LocalDate
    duration: number;
    distance: number;
  }