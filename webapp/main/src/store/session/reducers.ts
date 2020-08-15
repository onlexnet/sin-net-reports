import { SessionState, SessionAction } from "./types";
import _ from "lodash";

const initialState: SessionState = {
    loggedIn: false
}

export const sessionReducer = (state = initialState, action: SessionAction): SessionState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case "START_SESSION":
            clone.loggedIn = true;
            return clone;
        case "STOP_SESSION":
            clone.loggedIn = false;
            return clone;
        default:
            return state;
    }
}
