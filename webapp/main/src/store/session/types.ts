export enum SignInFlow {
    Unknown,
    SessionInitiated,
    SessionEstablished
}
export interface SessionState {
    flow: SignInFlow
}


export const INITIATE_SESSION = 'INITIATE_SESSION'

interface InitiateSessionAction {
    type: typeof INITIATE_SESSION
}

export type SessionAction = InitiateSessionAction;
