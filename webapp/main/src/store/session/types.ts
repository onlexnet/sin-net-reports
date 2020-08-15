import { Action } from "redux";

export enum SignInFlow {
    Unknown,
    SessionInitiated,
    SessionEstablished
}
export interface SessionState {
    flow: SignInFlow
}

export const INITIATE_SESSION_STARTED = 'INITIATE_SESSION_STARTED'
export const INITIATE_SESSION_FINISHED = 'INITIATE_SESSION_FINISHED'

interface InitiateSessionStartedAction extends Action<typeof INITIATE_SESSION_STARTED> {
}
interface InitiateSessionFinishedAction extends Action<typeof INITIATE_SESSION_FINISHED> {
}


export type SessionAction = InitiateSessionStartedAction | InitiateSessionFinishedAction;
