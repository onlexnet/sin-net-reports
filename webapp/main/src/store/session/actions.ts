import { SessionAction, INITIATE_SESSION } from "./types"

export const initiateSession = (): SessionAction => {
    return {
        type: INITIATE_SESSION
    }
}
