export enum SignInFlow {
    Unknown,
    SessionInitiated,
    SessionEstablished
}
export interface SessionState {
    flow: SignInFlow,
    idToken: string,
    email: string
}

/** Authorization request sent to B2C */
export const INITIATE_SESSION_STARTED = 'INITIATE_SESSION_STARTED';
/** Test login with email (for development/smoke tests) */
export const TEST_LOGIN_STARTED = 'TEST_LOGIN_STARTED';
/** User is successfully logged in to the System. */
export const INITIATE_SESSION_FINISHED = 'INITIATE_SESSION_FINISHED';


interface InitiateSessionStartedAction {
    type: typeof INITIATE_SESSION_STARTED
}

interface TestLoginStartedAction {
    type: typeof TEST_LOGIN_STARTED,
    email: string
}

export interface InitiateSessionFinishedAction {
    type: typeof INITIATE_SESSION_FINISHED,
    jwtToken: string,
    email: string
}


export type SessionAction = InitiateSessionStartedAction | TestLoginStartedAction | InitiateSessionFinishedAction;
