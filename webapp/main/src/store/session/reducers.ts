import { SessionState, SessionAction, SignInFlow } from "./types";
import _ from "lodash";

const initialState: SessionState = {
    flow: SignInFlow.Unknown
}

export const sessionReducer = (state = initialState, action: SessionAction): SessionState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case "INITIATE_SESSION_STARTED":
            console.log('initiate session started');
            clone.flow = SignInFlow.SessionInitiated;
            return clone;
        case "INITIATE_SESSION_FINISHED":
            console.log('initiate session finished');
            clone.flow = SignInFlow.SessionEstablished
            return clone;
        default:
            return state;
    }
}
