import { LocalDate } from "../viewcontext/TimePeriod";
import { v4 as uuid } from 'uuid';

export interface ServiceAppModel extends EntityId {
  servicemanName: string | undefined,
  description: string | undefined,
  when: LocalDate,
  duration: number | undefined,
  distance: number | undefined
  customer: {
    id: EntityId,
    name: string
  } | undefined
}

export interface EntityId {
  projectId: string,
  entityId: string,
  entityVersion: number,
}