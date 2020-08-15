export interface ServicesState {
    numerOfItems: number;
}

export const ADD_SERVICE = "ADD_SERVICE";
export const REMOVE_SERVICE = "REMOVE_SERVICE";

interface AddServiceAction {
    type: typeof ADD_SERVICE,
    payload: {
        description: string
    }
}

interface RemoveServiceAction {
    type: typeof REMOVE_SERVICE
    payload: {

    }
}

export type ServiceAction = AddServiceAction | RemoveServiceAction;
