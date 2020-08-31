import { Action } from "redux";
import { TimePeriod } from "./TimePeriod";

export interface ViewContextState {
    period: TimePeriod
}

export const VIEWCONTEXT_PREV_PERIOD = "VIEWCONTEXT_PREV_PERIOD";
export const VIEWCONTEXT_NEXT_PERIOD = "VIEWCONTEXT_NEXT_PERIOD";
export const VIEWCONTEXT_SELECT_PERIOD = "VIEWCONTEXT_SELECT_PERIOD";
const VIEWCONTEXT_PERIOD_SELECTED = "VIEWCONTEXT_PERIOD_SELECTED"

export interface PeriodPrev extends Action<typeof VIEWCONTEXT_PREV_PERIOD> {
    type: typeof VIEWCONTEXT_PREV_PERIOD,
    payload: { }
}

export interface PeriodNext extends Action<typeof VIEWCONTEXT_NEXT_PERIOD> {
    type: typeof VIEWCONTEXT_NEXT_PERIOD,
    payload: { }
}

export interface SelectPeriodCommand extends Action<typeof VIEWCONTEXT_SELECT_PERIOD> {
    type: typeof VIEWCONTEXT_SELECT_PERIOD,
    payload: {
        requested: TimePeriod
    }
}

export interface PeriodSelected extends Action<typeof VIEWCONTEXT_PERIOD_SELECTED> {
    type: typeof VIEWCONTEXT_PERIOD_SELECTED,
    payload: {
        requested: TimePeriod
    }
}

type ViewContextActionType = typeof VIEWCONTEXT_PREV_PERIOD
                           | typeof VIEWCONTEXT_NEXT_PERIOD
                           | typeof VIEWCONTEXT_SELECT_PERIOD
                           | typeof VIEWCONTEXT_PERIOD_SELECTED;

export type ViewContextAction = PeriodSelected
export type ViewContextCommand = PeriodPrev | PeriodNext | SelectPeriodCommand;
