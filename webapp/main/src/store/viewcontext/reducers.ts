import _ from "lodash";
import { ViewContextState, ViewContextAction } from "./types";
import { TimePeriod } from "./TimePeriod";

const initialState: ViewContextState = {
    period: new TimePeriod(new Date())
}

export const viewContextReducer = (state = initialState, action: ViewContextAction): ViewContextState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case 'VIEWCONTEXT_PERIOD_SELECTED':
            var period = action.payload.requested;
            clone.period = period;
            return clone;
        case 'VIEWCONTEXT_PREV_PERIOD':
            clone.period = clone.period.prev();
            return clone;
        case 'VIEWCONTEXT_NEXT_PERIOD':
            clone.period = clone.period.next();
            return clone;
        default:
            return state;
    }
}
