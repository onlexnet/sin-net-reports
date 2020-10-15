import { Action } from "redux";
import { TimePeriod } from "./TimePeriod";

export interface ViewContextState {
    period: TimePeriod,
    editedActionId: string | null
}

export const VIEWCONTEXT_PREV_PERIOD = "VIEWCONTEXT_PREV_PERIOD";
export const VIEWCONTEXT_NEXT_PERIOD = "VIEWCONTEXT_NEXT_PERIOD";
export const VIEWCONTEXT_PERIOD_SELECTED = "VIEWCONTEXT_PERIOD_SELECTED";
export const VIEWCONTEXT_ACTION_EDIT_START = "VIEWCONTEXT_ACTION_EDIT_START";
export const VIEWCONTEXT_ACTION_EDIT_CANCEL = "VIEWCONTEXT_ACTION_EDIT_CANCEL";

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

export interface ActionEditItem extends Action<typeof VIEWCONTEXT_ACTION_EDIT_START> {
    type: typeof VIEWCONTEXT_ACTION_EDIT_START,
    payload: {
        actionEntityId: string
    }
}

export interface ActionEditCancel extends Action<typeof VIEWCONTEXT_ACTION_EDIT_CANCEL> {
    type: typeof VIEWCONTEXT_ACTION_EDIT_CANCEL,
    payload: { }
}

export type ViewContextAction = PeriodSelected
                              | PeriodPrev
                              | PeriodNext
                              | ActionEditItem
                              | ActionEditCancel;
export type ViewContextCommand = PeriodPrev | PeriodNext;
