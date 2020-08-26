import { Action } from "redux";

export interface ViewContextState {
    period: Date
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


export type ViewcontextActionType = typeof VIEWCONTEXT_PREV_PERIOD |
                                    typeof VIEWCONTEXT_NEXT_PERIOD
export type ViewcontextAction = PeriodPrevious | PeriodNext
