import { combineReducers } from "redux";
import { serviceReducer } from "./services/reducers";
import { sessionReducer } from "./session/reducers";

export const rootReducer = combineReducers({
    auth: sessionReducer,
    services: serviceReducer
})
export type RootState = ReturnType<typeof rootReducer>;

