import { Action } from "redux";
import { TimePeriod } from "./TimePeriod";

export interface ViewContextState {
    period: TimePeriod,
    editedActionId: string | null
}

export const VIEWCONTEXT_PREV_PERIOD = "VIEWCONTEXT_PREV_PERIOD";
export const VIEWCONTEXT_NEXT_PERIOD = "VIEWCONTEXT_NEXT_PERIOD";
export const VIEWCONTEXT_PERIOD_SELECTED = "VIEWCONTEXT_PERIOD_SELECTED";
export const VIEWCONTEXT_ACTION_EDIT = "VIEWCONTEXT_ACTION_EDIT";

export interface PeriodPrev extends Action<typeof VIEWCONTEXT_PREV_PERIOD> {
    type: typeof VIEWCONTEXT_PREV_PERIOD,
    payload: { }
}

export interface PeriodNext extends Action<typeof VIEWCONTEXT_NEXT_PERIOD> {
    type: typeof VIEWCONTEXT_NEXT_PERIOD,
    payload: { }
}

export interface PeriodSelected extends Action<typeof VIEWCONTEXT_PERIOD_SELECTED> {
    type: typeof VIEWCONTEXT_PERIOD_SELECTED,
    payload: {
        requested: TimePeriod
    }
}

export interface ActionEditItem extends Action<typeof VIEWCONTEXT_ACTION_EDIT> {
    type: typeof VIEWCONTEXT_ACTION_EDIT,
    payload: {
        actionEntityId: string
    }
}

export type ViewContextAction = PeriodSelected
                              | PeriodPrev
                              | PeriodNext
                              | ActionEditItem;
export type ViewContextCommand = PeriodPrev | PeriodNext;
