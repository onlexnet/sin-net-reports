import { call, put, takeEvery } from 'redux-saga/effects';
import { Action } from 'redux';
import { SessionActionTypes } from '../session/types';
import { RELOAD_SERVICE_LIST } from '../services/types';
import { reloadServicesBegin } from '../services/actions';

const generateNewNumber = (): Promise<any> => {
  const promise = new Promise<number>(resolve => {
    setTimeout(() => {
      resolve();
    }, 500);
  });
  return promise;
};

const fetchUser = function* (action: Action) {
  console.log('saga + ' + JSON.stringify(action));
  try {
    const user = yield call(generateNewNumber);
    yield put({ type: SessionActionTypes.INITIATE_SESSION_FINISHED });
  } catch (e) {
    yield put({ type: "USER_FETCH_FAILED", message: e.message });
  }
}

export function* loginRequestSaga() {
  yield takeEvery(SessionActionTypes.INITIATE_SESSION_STARTED, fetchUser);
}

/**
 * Saga invoked when user is logged in to the application.
 * <p>
 * it is a central place to invoke some side-effects of logging loke set context of work,
 * initialize UI and load initial data.
*/
const reloadContext = function* () {
  try {
    const now = new Date();
    yield put(reloadServicesBegin(now.getFullYear(), now.getMonth()));
  } catch (e) {
    yield put({ type: "USER_FETCH_FAILED", message: e.message });
  }
}

export function* postLoginSaga() {
  yield takeEvery(SessionActionTypes.INITIATE_SESSION_FINISHED, reloadContext);
}