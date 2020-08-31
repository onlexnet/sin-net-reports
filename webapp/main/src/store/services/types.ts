import { ServiceAppModel } from "./ServiceModel";
import { Action } from "redux";
import { TimePeriod } from "../viewcontext/TimePeriod";

export interface ServicesState {
    numerOfItems: number;
    items: ServiceAppModel[];    
}

export const ADD_SERVICE_COMMAND = "ADD_SERVICE";
export const REMOVE_SERVICE = "REMOVE_SERVICE";
export const REFRESH_SERVICE_LIST_END = "REFRESH_SERVICE_LIST_END";
export const RELOAD_SERVICE_LIST = "RELOAD_SERVICE_LIST";

export interface AddServiceAction extends Action<typeof ADD_SERVICE_COMMAND> {
    type: typeof ADD_SERVICE_COMMAND,
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
    type: typeof REFRESH_SERVICE_LIST_END,
    payload: {
        items: ServiceAppModel[]
    }
}

export interface ReloadServiceList {
    type: typeof RELOAD_SERVICE_LIST,
    payload: { }
}

export type ServiceActionType = typeof ADD_SERVICE_COMMAND | typeof REMOVE_SERVICE | typeof REFRESH_SERVICE_LIST_END | typeof RELOAD_SERVICE_LIST;
export type ServiceAction = AddServiceAction | RemoveServiceAction | RefreshServiceAction | ReloadServiceList;
export enum ServiceCommands {
    ReloadServiceList
};
