import { ServiceAction } from "./types"

export const addService = (description: string): ServiceAction => {
    return {
        type: "ADD_SERVICE",
        payload: {
            description: description
        }
    }
}

export const removeService = (): ServiceAction => {
    return {
        type: "REMOVE_SERVICE",
        payload: {
        }
    }
}
