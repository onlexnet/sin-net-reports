import { SessionState, SessionAction, SignInFlow } from "./types";
import _ from "lodash";

export const initialSessionState: SessionState = {
    flow: SignInFlow.Unknown,
    idToken: "undefined",
    email: "invalid@email"
}

export const sessionReducer = (state: SessionState, action: SessionAction): SessionState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case 'INITIATE_SESSION_STARTED':
            clone.flow = SignInFlow.SessionInitiated;
            return clone;
        case 'INITIATE_SESSION_FINISHED':
            clone.flow = SignInFlow.SessionEstablished
            clone.idToken = action.jwtToken;
            clone.email = action.email;
            return clone;
        default:
            return state;
    }
}
