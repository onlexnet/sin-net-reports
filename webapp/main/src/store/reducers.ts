import { combineReducers } from "redux";
import { sessionReducer } from "./session/reducers";
import { viewContextReducer } from "./viewcontext/reducers";

export const rootReducer = combineReducers({
    auth: sessionReducer,
    viewContext: viewContextReducer
})
export type RootState = ReturnType<typeof rootReducer>;

