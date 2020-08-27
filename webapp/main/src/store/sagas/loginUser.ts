import { call, put, takeEvery } from 'redux-saga/effects';
import { Action } from 'redux';
import { SessionActionTypes } from '../session/types';

let initialNumber = 0;
const generateNewNumber = (delay: number): Promise<number> => {
  const promise = new Promise<number>(resolve => {
    setTimeout(() => {
      initialNumber += 1;
      resolve(initialNumber);
    }, delay);
  });
  return promise;
};

const fetchUser = function* (action: Action) {
  console.log('saga + ' + JSON.stringify(action));
  try {
    const user = yield call(generateNewNumber, 500);
    yield put({ type: SessionActionTypes.INITIATE_SESSION_FINISHED });
  } catch (e) {
    yield put({ type: "USER_FETCH_FAILED", message: e.message });
  }
}

export function* loginRequestSaga() {
  yield takeEvery(SessionActionTypes.INITIATE_SESSION_STARTED, fetchUser);
}