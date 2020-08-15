import { createStore, Reducer, compose, combineReducers } from "redux";
import { serviceReducer } from "./services/reducers";
import { sessionReducer } from "./session/reducers";

// add redux devtools extension as a property/method in window
declare global {
  interface Window {
    __REDUX_DEVTOOLS_EXTENSION__?: typeof compose;
  }
}

const rootReducer = combineReducers({
  auth: sessionReducer,
  services: serviceReducer
})

export const store = createStore(
  rootReducer,
  // Enable the Redux DevTools extension if available
  /// See more: https://chrome.google.com/webstore/detail/redux-devtools/lmhkpmbekcpmknklioeibfkpmmfiblj
  window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__()
);

export type RootState = ReturnType<typeof rootReducer>