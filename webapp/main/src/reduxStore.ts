import { createStore, Reducer, compose, combineReducers } from "redux";
import { AuthenticationActions, AuthenticationState } from "react-aad-msal";
import { serviceReducer } from "./store/services/reducers";

// add redux devtools extension as a property/method in window
declare global {
  interface Window {
    __REDUX_DEVTOOLS_EXTENSION__?: typeof compose;
  }
}

export const initialState = {
  initializing: false,
  initialized: false,
  idToken: null,
  accessToken: null,
  fakeRows: 3,
  state: AuthenticationState.Unauthenticated
};

const sessionReducer = (
  state = initialState,
  action: { type: any; payload: { account: any } }
): typeof initialState => {
  switch (action.type) {
    case AuthenticationActions.Initializing:
      return {
        ...state,
        initializing: true,
        initialized: false
      };
    case AuthenticationActions.Initialized:
      return {
        ...state,
        initializing: false,
        initialized: true
      };
    case AuthenticationActions.AcquiredIdTokenSuccess:
      return {
        ...state
      };
    case AuthenticationActions.AcquiredAccessTokenSuccess:
      return {
        ...state
      };
    case AuthenticationActions.AcquiredAccessTokenError:
      return {
        ...state,
        accessToken: null
      };
    case AuthenticationActions.LoginSuccess:
      return {
        ...state
      };
    case AuthenticationActions.LoginError:
    case AuthenticationActions.AcquiredIdTokenError:
    case AuthenticationActions.LogoutSuccess:
      return { ...state, idToken: null, accessToken: null };
    case AuthenticationActions.AuthenticatedStateChanged:
      return {
        ...state
      };
    default:
      return state;
  }
};

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
export const anyStore: any = store;

export type RootState = ReturnType<typeof rootReducer>