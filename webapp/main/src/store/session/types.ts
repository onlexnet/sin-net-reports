export interface SessionState {
    loggedIn: boolean
}

export const START_SESSION = "START_SESSION";
export const STOP_SESSION = "STOP_SESSION";

interface StartSessionAction {
    type: typeof START_SESSION,
    payload: {
    }
}

interface StopSessionAction {
    type: typeof STOP_SESSION,
    payload: {
    }
}

export type SessionAction = StartSessionAction | StopSessionAction;
