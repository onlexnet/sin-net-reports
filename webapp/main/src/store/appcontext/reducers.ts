import _ from "lodash";
import { AppContext } from "./types";

const initialState: AppContext = {
    type: "APPCONTEXT_EMPTY"
}

export const appContextReducer = (state = initialState, action: AppContext): AppContext => {
    return action;
}
