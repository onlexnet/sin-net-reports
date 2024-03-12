import _ from "lodash";
import { appContextReducer, initialAppState } from "./appcontext/reducers";
import { initialSessionState, sessionReducer } from "./session/reducers";
import { initialViewContextState, viewContextReducer } from "./viewcontext/reducers";
import { SessionAction, SessionState } from "./session/types";
import { AppContextAction, AppState } from "./appcontext/types";
import { ViewContextAction, ViewContextCommand, ViewContextState } from "./viewcontext/types";

export interface RootState {
    auth: SessionState,
    appState: AppState,
    viewContext: ViewContextState
}

export const initialState: RootState = {
    auth: initialSessionState,
    appState: initialAppState,
    viewContext: initialViewContextState
};

export const reducer = (state: RootState, action: SessionAction | AppContextAction | ViewContextCommand | ViewContextAction): RootState => {
    let { auth, appState, viewContext} = state
    switch (action.type) {
        case "APPCONTEXT_EMPTY":
        case "APPCONTEXT_READY":
            appState = appContextReducer(appState, action);
            break;
        case "INITIATE_SESSION_FINISHED":
        case "INITIATE_SESSION_STARTED":
            auth = sessionReducer(auth, action);
            break;
        case "VIEWCONTEXT_NEXT_PERIOD":
        case "VIEWCONTEXT_PREV_PERIOD":
            viewContext = viewContextReducer(viewContext, action);
            break;
        case "VIEWCONTEXT_ACTION_EDIT_CANCEL":
        case "VIEWCONTEXT_ACTION_EDIT_START":
        case "VIEWCONTEXT_ACTION_EDIT_UPDATED":
        case "VIEWCONTEXT_PERIOD_SELECTED":

    }
    return { auth, appState, viewContext};
}

