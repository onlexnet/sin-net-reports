import { ServiceAction, ServicesState } from "./types";
import _ from "lodash";

const initialState: ServicesState = {
    items: []
}

export const serviceReducer = (state = initialState, action: ServiceAction): ServicesState => {
    const clone = _.cloneDeep(state);
    switch (action.type) {
        case 'ADD_SERVICE':
            clone.items.push({
                description: action.payload.description,
                servicemanName: action.payload.serviceMan
            });
            return clone;
        case "REMOVE_SERVICE":
            return Object.assign({}, state, {
            });
        case "REFRESH_SERVICE_LIST_END":
            clone.items = action.payload.items;
            return clone;
        default:
            return state;
    }
}
