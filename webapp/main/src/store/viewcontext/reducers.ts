import _ from "lodash";
import { ViewContextState, ViewContextAction } from "./types";
import { LocalDate } from "./LocalDate";

const initialState: ViewContextState = {
    date: new LocalDate(new Date())
}

export const viewContextReducer = (state = initialState, action: ViewContextAction): ViewContextState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case "VIEWCONTEXT_NEXT_PERIOD": {
            clone.date = clone.date.next();
            return clone;
        }
        case "VIEWCONTEXT_PREV_PERIOD": {
            const current = clone.date;
            clone.date = clone.date.prev();
            return clone;
        }
        default:
            return state;
    }
}
