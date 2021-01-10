import { LocalDate } from "../viewcontext/TimePeriod";

export interface ServiceAppModel extends EntityId {
  servicemanName: string | undefined,
  description: string | undefined,
  customerName: string | undefined,
  when: LocalDate,
  duration: number | undefined,
  distance: number | undefined
}

export interface EntityId {
  projectId: string,
  entityId: string,
  entityVersion: number,
}