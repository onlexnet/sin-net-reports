import { ServiceAction } from "./types"
import { ServiceAppModel } from "./ServiceModel"

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

export const reloadServicesBegin = (year: number, month: number): ServiceAction => {
    return {
        type: "REFRESH_SERVICE_LIST_BEGIN",
        payload: {
            year,
            month,
        }
    };
}

export const reloadServicesEnd = (items: ServiceAppModel[]): ServiceAction => {
    return {
        type: "REFRESH_SERVICE_LIST_END",
        payload: {
            items: items
        }
    };
}
