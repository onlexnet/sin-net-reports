import { LocalDate } from "../viewcontext/TimePeriod";

export interface ServiceAppModel extends EntityId {
  servicemanName: string,
  description: string,
  customerName: string,
  when: LocalDate,
  duration: number,
  distance: number
}

export interface EntityId {
  entityId: string,
  entityVersion: number,
}