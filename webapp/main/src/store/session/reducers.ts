import { SessionState, SessionAction } from "./types";
import _ from "lodash";

const initialState: SessionState = {
    loggedIn: false
}

export const sessionReducer = (state = initialState, action: SessionAction): SessionState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case "UPDATE_SESSION":
            alert('Update session');
            clone.loggedIn = true;
            return clone;
        default:
            return state;
    }
}
