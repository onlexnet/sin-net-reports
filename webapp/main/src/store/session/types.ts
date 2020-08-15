export interface SessionState {
    loggedIn: boolean
}

export const UPDATE_SESSION = 'UPDATE_SESSION'
  
interface UpdateSessionAction {
    type: typeof UPDATE_SESSION
    payload: SessionState
}
  
export type SessionAction = UpdateSessionAction;
  