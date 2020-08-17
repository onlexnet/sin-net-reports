import { ServiceAppModel } from "./ServiceModel";

export interface ServicesState {
    numerOfItems: number;
    items: ServiceAppModel[];    
}

export const ADD_SERVICE = "ADD_SERVICE";
export const REMOVE_SERVICE = "REMOVE_SERVICE";
export const REFRESH_SERVICE_LIST = "REFRESH_SERVICE_LIST";
export const REFRESH_SERVICE_LIST_REQUEST = "REFRESH_SERVICE_LIST_REQUEST";

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

interface RefreshServiceAction {
    type: typeof REFRESH_SERVICE_LIST
}

interface RefreshServiceAction {
    type: typeof REFRESH_SERVICE_LIST,
    payload: {
        items: ServiceAppModel[]
    }
}

interface RefreshServiceRequestAction {
    type: typeof REFRESH_SERVICE_LIST_REQUEST
}

export type ServiceActionType = typeof ADD_SERVICE | typeof REMOVE_SERVICE | typeof REFRESH_SERVICE_LIST | typeof REFRESH_SERVICE_LIST_REQUEST;
export type ServiceAction = AddServiceAction | RemoveServiceAction | RefreshServiceAction | RefreshServiceRequestAction;
