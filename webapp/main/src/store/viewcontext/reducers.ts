import _ from "lodash";
import { ViewContextState, ViewcontextAction } from "./types";

const initialState: ViewContextState = {
    period: new Date()
}

export const serviceReducer = (state = initialState, action: ViewcontextAction): ViewContextState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case "VIEWCONTEXT_NEXT_PERIOD": {
            const current = clone.period;
            const newDate = new Date(current.getFullYear(), current.getMonth() + 1);
            return Object.assign({}, clone, {
                period: newDate
            });
        }
        case "VIEWCONTEXT_PREV_PERIOD": {
            const current = clone.period;
            const newDate = new Date(current.getFullYear(), current.getMonth() -1);
            return Object.assign({}, clone, {
                period: newDate
            });
        }
        default:
            return state;
    }
}
