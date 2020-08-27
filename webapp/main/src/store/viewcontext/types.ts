import { Action } from "redux";
import { TimePeriod } from "./TimePeriod";

export interface ViewContextState {
    period: TimePeriod
}

const VIEWCONTEXT_PREV_PERIOD = "VIEWCONTEXT_PREV_PERIOD";
const VIEWCONTEXT_NEXT_PERIOD = "VIEWCONTEXT_NEXT_PERIOD";

export interface PeriodPrevious extends Action<typeof VIEWCONTEXT_PREV_PERIOD> {
    type: typeof VIEWCONTEXT_PREV_PERIOD,
    payload: { }
}

export interface PeriodNext extends Action<typeof VIEWCONTEXT_NEXT_PERIOD> {
    type: typeof VIEWCONTEXT_NEXT_PERIOD,
    payload: { }
}


type ViewContextActionType = typeof VIEWCONTEXT_PREV_PERIOD |
                                    typeof VIEWCONTEXT_NEXT_PERIOD
export type ViewContextAction = PeriodPrevious | PeriodNext