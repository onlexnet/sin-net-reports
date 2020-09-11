import { call, put, takeEvery, takeLatest, select } from 'redux-saga/effects';
import { sdk } from '../../api';
import { FetchServicesQuery } from '../../api/generated';
import { ServiceAppModel } from '../actions/ServiceModel';
import { reloadServicesEnd } from '../actions/actions';
import { RootState } from '../reducers';
import { ViewContextState, VIEWCONTEXT_SELECT_PERIOD, SelectPeriodCommand, VIEWCONTEXT_NEXT_PERIOD, PeriodNext, VIEWCONTEXT_PREV_PERIOD, PeriodPrev } from '../viewcontext/types';
import { TimePeriod } from '../viewcontext/TimePeriod';
import { asDtoDates } from '../../api/Mapper';
import { periodSelected, selectPeriodCommand } from '../viewcontext/actions';
import { toModel } from '../../api/DtoMapper';

/**
 * 1) sets new date
 * 2) load data related to selected period
 * @param command 
 */
const selectPeriod = function* (command: SelectPeriodCommand) {
  try {

    const requested = command.payload.requested;

    yield put(periodSelected(requested))
    
    const periodDto = asDtoDates(requested);
    const result = (yield call(sdk.FetchServices, {
      from: periodDto.dateFrom,
      to: periodDto.dateTo
    })) as FetchServicesQuery;

    var items = result.Services.search.items
      .map(it => {
        const item: ServiceAppModel = {
          description: it.description,
          servicemanName: it.servicemanName ?? "-",
          customerName: it.forWhatCustomer,
          when: toModel(it.whenProvided),
          distance: it.distance,
          duration: it.duration
        }
        return item;
      });
    yield put(reloadServicesEnd(items));
  } catch (e) {
    yield put({ type: "SOMETHING_T_DO_NOT_YET_DEFINED", message: e.message });
  }
}

/*
  Starts fetchUser on each dispatched `USER_FETCH_REQUESTED` action.
  Allows concurrent fetches of user.
*/
export function* selectPeriodSaga(period: TimePeriod) {
  yield takeLatest(VIEWCONTEXT_SELECT_PERIOD, selectPeriod);
}


const nextPeriod = function* (command: PeriodNext) {
  const viewContext: ViewContextState = yield select((s: RootState) => s.viewContext);
  yield put(selectPeriodCommand(viewContext.period.next()))
}

const prevPeriod = function* (command: PeriodPrev) {
  const viewContext: ViewContextState = yield select((s: RootState) => s.viewContext);
  yield put(selectPeriodCommand(viewContext.period.prev()))
}


export function* nextPeriodSaga() {
  yield takeLatest(VIEWCONTEXT_NEXT_PERIOD, nextPeriod);
}

export function* prevPeriodSaga() {
  yield takeLatest(VIEWCONTEXT_PREV_PERIOD, prevPeriod);
}

