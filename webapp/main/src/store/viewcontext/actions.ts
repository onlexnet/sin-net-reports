import { ViewContextAction } from "./types"

export const nextPeriod = (): ViewContextAction => {
    return {
        type: "VIEWCONTEXT_NEXT_PERIOD",
        payload: { }
    }
}

export const previousPeriod = (): ViewContextAction => {
    return {
        type: "VIEWCONTEXT_PREV_PERIOD",
        payload: { }
    }
}