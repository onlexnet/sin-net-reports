import { ServiceAction, ServicesState } from "./types";
import _ from "lodash";

const initialState: ServicesState = {
    numerOfItems: 10,
    items: []
}

export const serviceReducer = (state = initialState, action: ServiceAction): ServicesState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case "ADD_SERVICE":
            return Object.assign({}, state, {
                numerOfItems: state.numerOfItems + 1
            });
        case "REMOVE_SERVICE":
            return Object.assign({}, state, {
                numerOfItems: state.numerOfItems - 1
            });
        case "REFRESH_SERVICE_LIST_END":
            clone.numerOfItems = 30;
            clone.items = action.payload.items;
            return clone;
        default:
            return state;
    }
}
