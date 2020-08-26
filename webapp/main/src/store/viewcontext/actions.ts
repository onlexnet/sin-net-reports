import { ViewcontextAction } from "./types"

export const nextPeriod = (): ViewcontextAction => {
    return {
        type: "VIEWCONTEXT_NEXT_PERIOD",
        payload: { }
    }
}

export const previousPeriod = (): ViewcontextAction => {
    return {
        type: "VIEWCONTEXT_PREV_PERIOD",
        payload: { }
    }
}