import { initialState } from "../reduxStore";

interface AddService {
    type: 'ADD_SERVICE',
    payload: {
        description: string
    }
}

interface RemoveService {
    type: 'REMOVE_SERVICE'
    payload: {

    }
}

export type ServiceAction = AddService
    | RemoveService;

export const addService = (description: string): AddService => {
    return {
        type: "ADD_SERVICE",
        payload: {
            description: description
        }
    }
}

export const removeService = (): RemoveService => {
    return {
        type: "REMOVE_SERVICE",
        payload: {
        }
    }
}

export const serviceReducer = (state = initialState, action: ServiceAction) => {
    if (action.type === "ADD_SERVICE") {
        state.fakeRows = state.fakeRows + 1;
    }
    if (action.type === "REMOVE_SERVICE") {
        state.fakeRows = state.fakeRows - 1;
    }

    return state;
}