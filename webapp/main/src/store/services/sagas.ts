import { call, put, takeEvery, takeLatest } from 'redux-saga/effects';
import { Action } from 'redux';
import { sdk } from '../../api';
import { FetchServicesQuery, ServiceModel } from '../../api/generated';
import { REFRESH_SERVICE_LIST_REQUEST, REFRESH_SERVICE_LIST } from './types';
import { ServiceAppModel } from './ServiceModel';
import { reloadServicesEnd } from './actions';

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

const fetchServices = function* (action: Action<typeof REFRESH_SERVICE_LIST_REQUEST>) {
  try {
    const result = (yield call(sdk.FetchServices, {
      from: "2010/01/01",
      to: "2030/01/01"
    })) as FetchServicesQuery;

    var items = result.Services.search.items
      .map(it => {
        const item: ServiceAppModel = {
          description: it.forWhatCustomer ?? "-"
        }
        return item;
      });
    yield put(reloadServicesEnd(items));
  } catch (e) {
    yield put({ type: "USER_FETCH_FAILED", message: e.message });
  }
}

/*
  Starts fetchUser on each dispatched `USER_FETCH_REQUESTED` action.
  Allows concurrent fetches of user.
*/
export function* reloadServiceSaga() {
  yield takeLatest(REFRESH_SERVICE_LIST_REQUEST, fetchServices);
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


