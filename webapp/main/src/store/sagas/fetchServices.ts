import { call, put, takeEvery, takeLatest } from 'redux-saga/effects';
import { Action } from 'redux';
import { sdk } from '../../api';
import { FetchServicesQuery, ServiceModel, NewServiceActionMutation } from '../../api/generated';
import { REFRESH_SERVICE_LIST_REQUEST, REFRESH_SERVICE_LIST, ADD_SERVICE, AddServiceAction } from '../services/types';
import { ServiceAppModel } from '../services/ServiceModel';
import { reloadServicesEnd, reloadServicesBegin } from '../services/actions';

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
