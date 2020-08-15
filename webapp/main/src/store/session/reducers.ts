import { SessionState, SessionAction, SignInFlow } from "./types";
import _ from "lodash";

const initialState: SessionState = {
    flow: SignInFlow.Unknown
}

export const sessionReducer = (state = initialState, action: SessionAction): SessionState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case "INITIATE_SESSION":
            alert('initiate session');
            clone.flow = SignInFlow.SessionInitiated;
            return clone;
        default:
            return state;
    }
}
