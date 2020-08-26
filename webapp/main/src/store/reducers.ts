import { combineReducers } from "redux";
import { serviceReducer } from "./services/reducers";
import { sessionReducer } from "./session/reducers";
import { viewcontextReducer } from "./viewcontext/reducers";

export const rootReducer = combineReducers({
    auth: sessionReducer,
    services: serviceReducer,
    viewContext: viewcontextReducer
})
export type RootState = ReturnType<typeof rootReducer>;

