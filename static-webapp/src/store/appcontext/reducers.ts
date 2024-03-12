import { AppContextAction, AppState } from "./types";

export const initialAppState: AppState = {
    empty: true,
    projectId: 'invalid UUID [1]'
}

export const appContextReducer = (state: AppState, action: AppContextAction): AppState => {
    if (action.type === "APPCONTEXT_READY") {
        return { empty: false, projectId: action.projectId };
    }
    return state;
}
