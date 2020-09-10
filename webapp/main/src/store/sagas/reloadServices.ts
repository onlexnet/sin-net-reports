import { call, put, takeLatest, select } from 'redux-saga/effects';
import { sdk } from '../../api';
import { FetchServicesQuery } from '../../api/generated';
import { RELOAD_SERVICE_LIST, ReloadServiceList,  } from '../services/types';
import { ServiceAppModel } from '../services/ServiceModel';
import { reloadServicesEnd } from '../services/actions';
import { RootState } from '../reducers';
import { ViewContextState } from '../viewcontext/types';
import { asDtoDates } from '../../api/Mapper';

const fetchActions = function* (action: ReloadServiceList) {
  try {
    const viewContext: ViewContextState = yield select((s: RootState) => s.viewContext);
    const periodDto = asDtoDates(viewContext.period);
    const result = (yield call(sdk.FetchServices, {
      from: periodDto.dateFrom,
      to: periodDto.dateTo
    })) as FetchServicesQuery;

    var items = result.Services.search.items
      .map(it => {
        const item: ServiceAppModel = {
          description: it.description ?? "-",
          servicemanName: it.servicemanName ?? "-",
          customerName: it.forWhatCustomer,
          when: it.whenProvided,
          distance: it.distance,
          duration: it.duration
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
export function* reloadActionsSaga() {
  yield takeLatest(RELOAD_SERVICE_LIST, fetchActions);
}
