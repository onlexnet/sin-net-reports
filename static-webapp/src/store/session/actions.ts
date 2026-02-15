import { SessionAction, INITIATE_SESSION_STARTED, TEST_LOGIN_STARTED } from "./types"

export const initiateSession = (): SessionAction => {
    return {
        type: INITIATE_SESSION_STARTED
    }
}

export const testLogin = (email: string): SessionAction => {
    return {
        type: TEST_LOGIN_STARTED,
        email: email
    }
}

