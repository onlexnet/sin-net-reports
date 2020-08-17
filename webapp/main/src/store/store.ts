import { createStore, compose, combineReducers, applyMiddleware } from "redux";
import createSagaMiddleware from 'redux-saga'
import { composeWithDevTools } from 'redux-devtools-extension';
import { rootReducer } from "./reducers";
import { loginRequestSaga } from "./session/login.saga";
import { reloadServiceSaga } from "./services/sagas";

const sagaMiddleware = createSagaMiddleware();
const enhancers = composeWithDevTools(applyMiddleware(sagaMiddleware));
export const store = createStore(
  rootReducer,
  {},
  enhancers
);

sagaMiddleware.run(loginRequestSaga);
sagaMiddleware.run(reloadServiceSaga);