import { SessionAction, SessionState } from "./types"

import { UPDATE_SESSION } from './types'

export const updateSession = (newSession: SessionState): SessionAction => {
    return {
        type: UPDATE_SESSION,
        payload: newSession
    }
}
