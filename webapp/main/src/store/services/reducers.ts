import { ServiceAction, ServicesState } from "./types";

const initialState: ServicesState = {
    numerOfItems: 10
}

export const serviceReducer = (state = initialState, action: ServiceAction): ServicesState => {
    switch (action.type) {
        case "ADD_SERVICE":
            return Object.assign({}, state, {
                numerOfItems: state.numerOfItems + 1
            });
        case "REMOVE_SERVICE":
            return Object.assign({}, state, {
                numerOfItems: state.numerOfItems - 1
            });
        default:
            return state;
    }
}
