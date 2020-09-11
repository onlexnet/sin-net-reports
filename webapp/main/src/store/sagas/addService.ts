import { call, put, select, takeEvery, takeLatest } from 'redux-saga/effects';
import { reloadServicesBegin } from '../services/actions';
import { AddServiceCommand, ADD_SERVICE_COMMAND } from '../services/types';
import { sdk } from '../../api';
import { NewServiceActionMutation, NewServiceActionMutationVariables } from '../../api/generated';
import { RootState } from '../reducers';
import { ViewContextState } from '../viewcontext/types';
import { asDtoDates } from '../../api/Mapper';

const addService = function* (action: AddServiceCommand) {
    try {
        const state: ViewContextState = yield select((it: RootState) => it.viewContext);
        const { dateFrom } = asDtoDates(state.period);
        
        
        const result = (yield call(sdk.newServiceAction, {
            when: dateFrom,
            what: action.payload.description,
            who: action.payload.serviceMan,
            whom: action.payload.customerName,
            duration: action.payload.duration,
            distance: action.payload.distance
        } as NewServiceActionMutationVariables )) as NewServiceActionMutation;

        var addNewResult = result.Services.addNew;
        if (addNewResult) {
            yield put(reloadServicesBegin());
        }
    } catch (e) {
        yield put({ type: "USER_FETCH_FAILED", message: e.message });
    }
}

export function* addServiceSaga() {
    yield takeEvery(ADD_SERVICE_COMMAND, addService);
}
