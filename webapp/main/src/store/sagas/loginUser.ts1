import { call, put, takeEvery, select } from 'redux-saga/effects';
import { Action } from 'redux';
import { RootState } from '../reducers';
import { authModule } from '../../msal/autorun';
import { INITIATE_SESSION_FINISHED, INITIATE_SESSION_STARTED } from '../session/types';

const fetchUser = function* (action: Action) {
  console.log('saga + ' + JSON.stringify(action));
  try {
    const user = yield call(authModule.login);
    yield put({ type: INITIATE_SESSION_FINISHED });
  } catch (e) {
    yield put({ type: "USER_FETCH_FAILED", message: e.message });
  }
}

export function* loginRequestSaga() {
  yield takeEvery(INITIATE_SESSION_STARTED, fetchUser);
}

/**
 * Saga invoked when user is logged in to the application.
 * <p>
 * it is a central place to invoke some side-effects of logging to set context of work,
 * initialize UI and load initial data.
*/
const reloadContext = function* () {
  try {
    const state = yield select((it: RootState) => it.viewContext);
  } catch (e) {
    yield put({ type: "USER_FETCH_FAILED", message: e.message });
  }
}

export function* postLoginSaga() {
  yield takeEvery(INITIATE_SESSION_FINISHED, reloadContext);
}