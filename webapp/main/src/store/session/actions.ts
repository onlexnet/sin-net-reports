import { SessionAction, SessionActionTypes } from "./types"

export const initiateSession = (): SessionAction => {
    return {
        type: SessionActionTypes.INITIATE_SESSION_STARTED
    }
}

