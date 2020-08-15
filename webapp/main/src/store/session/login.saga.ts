import { call, put, takeEvery, takeLatest } from 'redux-saga/effects';
import { INITIATE_SESSION_STARTED, INITIATE_SESSION_FINISHED } from './types';
import { Action } from 'redux';

let initialNumber = 0;
export const generateNewNumber = (delay: number): Promise<number> => {
  const promise = new Promise<number>(resolve => {
    setTimeout(() => {
      initialNumber += 1;
      resolve(initialNumber);
    }, delay);
  });
  return promise;
};

function* fetchUser(action: Action) {
  console.log('saga + ' + JSON.stringify(action));
  try {
    const user = yield call(generateNewNumber, 2000);
    yield put({ type: INITIATE_SESSION_FINISHED });
  } catch (e) {
    yield put({ type: "USER_FETCH_FAILED", message: e.message });
  }
}

/*
  Starts fetchUser on each dispatched `USER_FETCH_REQUESTED` action.
  Allows concurrent fetches of user.
*/
export function* loginRequestSaga() {
  yield takeEvery(INITIATE_SESSION_STARTED, fetchUser);
}

// /*
//   Alternatively you may use takeLatest.

//   Does not allow concurrent fetches of user. If "USER_FETCH_REQUESTED" gets
//   dispatched while a fetch is already pending, that pending fetch is cancelled
//   and only the latest one will be run.
// */
// function* loginRequestSaga() {
//   yield takeLatest("USER_FETCH_REQUESTED", fetchUser);
// }

// export default loginRequestSaga;

// import { numberRequestCompletedAction } from '../actions';
// import { INITIATE_SESSION } from './types';

// export function* watchNewGeneratedNumberRequestStart() {
//   yield takeEvery(
//     INITIATE_SESSION,
//     requestNewGeneratedNumber
//   );
// }

// function* requestNewGeneratedNumber() {
//   const generatedNumber = yield call(generateNewNumber);
//   yield put(numberRequestCompletedAction(generatedNumber));
// }


