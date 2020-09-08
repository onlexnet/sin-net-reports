import { Action } from "redux";

export enum SignInFlow {
    Unknown,
    SessionInitiated,
    SessionEstablished
}
export interface SessionState {
    flow: SignInFlow,
    idToken: string | null
}

export const SessionActionTypes = {
    INITIATE_SESSION_STARTED: 'INITIATE_SESSION_STARTED',
    /** User is successfully logged in to the System. */
    INITIATE_SESSION_FINISHED: 'INITIATE_SESSION_FINISHED'
}


interface InitiateSessionStartedAction extends Action<typeof SessionActionTypes.INITIATE_SESSION_STARTED> {
}
interface InitiateSessionFinishedAction extends Action<typeof SessionActionTypes.INITIATE_SESSION_FINISHED> {
    idToken: string
}


export type SessionAction = InitiateSessionStartedAction | InitiateSessionFinishedAction;
