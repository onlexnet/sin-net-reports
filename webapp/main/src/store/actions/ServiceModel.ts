import { LocalDate } from "../viewcontext/TimePeriod";

export interface ServiceAppModel {
  entityId: string,
  entityVersion: number,
  servicemanName: string,
  description: string,
  customerName: string,
  when: LocalDate,
  duration: number,
  distance: number
}