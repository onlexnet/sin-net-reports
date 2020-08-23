import { createStore, compose, combineReducers, applyMiddleware } from "redux";
import createSagaMiddleware from 'redux-saga'
import { composeWithDevTools } from 'redux-devtools-extension';
import { rootReducer } from "./reducers";
import { registerWithMiddleware } from "./sagas";

export const sagaMiddleware = createSagaMiddleware();
const enhancers = composeWithDevTools(applyMiddleware(sagaMiddleware));
export const store = createStore(
  rootReducer,
  {},
  enhancers
);

registerWithMiddleware(sagaMiddleware);
