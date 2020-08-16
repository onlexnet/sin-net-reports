import { Action } from "redux";

export enum SignInFlow {
    Unknown,
    SessionInitiated,
    SessionEstablished
}
export interface SessionState {
    flow: SignInFlow
}

export const SessionActionTypes = {
    INITIATE_SESSION_STARTED: 'INITIATE_SESSION_STARTED',
    INITIATE_SESSION_FINISHED: 'INITIATE_SESSION_FINISHED'
}


interface InitiateSessionStartedAction extends Action<typeof SessionActionTypes.INITIATE_SESSION_FINISHED> {
}
interface InitiateSessionFinishedAction extends Action<typeof SessionActionTypes.INITIATE_SESSION_FINISHED> {
}


export type SessionAction = InitiateSessionStartedAction | InitiateSessionFinishedAction;
